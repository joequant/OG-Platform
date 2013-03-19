/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.compilation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import com.google.common.base.Supplier;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.position.Portfolio;
import com.opengamma.core.position.Position;
import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetResolver;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.depgraph.DependencyGraph;
import com.opengamma.engine.depgraph.DependencyGraphBuilder;
import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.depgraph.DependencyNodeFormatter;
import com.opengamma.engine.depgraph.Housekeeper;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.target.ComputationTargetReference;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.engine.view.ResultModelDefinition;
import com.opengamma.engine.view.ResultOutputMode;
import com.opengamma.engine.view.ViewCalculationConfiguration;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.util.tuple.Pair;

/**
 * Ultimately produces a set of {@link DependencyGraph}s from a {@link ViewDefinition}, one for each {@link ViewCalculationConfiguration}. Additional information, such as the live data requirements,
 * is collected along the way and exposed after compilation.
 * <p>
 * The compiled graphs are guaranteed to be calculable for at least the requested timestamp. One or more of the referenced functions may not be valid at other timestamps.
 */
public final class ViewDefinitionCompiler {

  private static final Logger s_logger = LoggerFactory.getLogger(ViewDefinitionCompiler.class);
  private static final boolean OUTPUT_DEPENDENCY_GRAPHS = false;
  private static final boolean OUTPUT_LIVE_DATA_REQUIREMENTS = false;
  private static final boolean OUTPUT_FAILURE_REPORTS = false;

  private ViewDefinitionCompiler() {
  }

  //-------------------------------------------------------------------------

  /**
   * Compiles the specified view definition wrt the supplied compilation context, valuation time and version correction and returns the compiled view. This method wraps the compileTask method, waiting
   * for completion of the async compilation task and returning the resulting CompiledViewDefinitionWithGraphsImpl, rather than a future reference to it.
   * 
   * @param viewDefinition the view definition to compile
   * @param compilationServices the compilation context (market data availability provider, graph builder factory, etc.)
   * @param valuationTime the effective valuation time against which to compile
   * @param versionCorrection the version correction to use
   * @return the CompiledViewDefinitionWithGraphsImpl that results from the compilation
   */
  protected static final class CompilationCompletionEstimate implements Housekeeper.Callback<Supplier<Double>> {

    private final String _label;
    private final ConcurrentMap<String, Double> _buildEstimates;

    private CompilationCompletionEstimate(final ViewCompilationContext context) {
      final Collection<DependencyGraphBuilder> builders = context.getBuilders();
      _buildEstimates = new ConcurrentHashMap<String, Double>();
      for (final DependencyGraphBuilder builder : builders) {
        _buildEstimates.put(builder.getCalculationConfigurationName(), 0d);
        Housekeeper.of(builder, this, builder.buildFractionEstimate()).start();
      }
      _label = context.getViewDefinition().getName();
    }

    public double[] estimates() {
      final double[] result = new double[_buildEstimates.size()];
      int i = 0;
      for (final Double estimate : _buildEstimates.values()) {
        result[i++] = estimate;
      }
      return result;
    }

    public double estimate() {
      double result = 0;
      for (final Double estimate : _buildEstimates.values()) {
        result += estimate;
      }
      return result / _buildEstimates.size();
    }

    @Override
    public boolean tick(final DependencyGraphBuilder builder, final Supplier<Double> estimate) {
      final Double estimateValue = estimate.get();
      s_logger.debug("{}/{} building at {}", new Object[] {_label, builder.getCalculationConfigurationName(), estimateValue });
      _buildEstimates.put(builder.getCalculationConfigurationName(), estimateValue);
      return estimateValue < 1d;
    }

    @Override
    public boolean cancelled(final DependencyGraphBuilder builder, final Supplier<Double> estimate) {
      return false;
    }

    @Override
    public boolean completed(final DependencyGraphBuilder builder, final Supplier<Double> estimate) {
      return estimate.get() < 1d;
    }
  }

  // TODO: return something that provides the caller with access to a completion metric to feedback to any interactive user
  private abstract static class CompilationTask implements Future<CompiledViewDefinitionWithGraphsImpl> {

    private final ViewCompilationContext _viewCompilationContext;
    private volatile CompiledViewDefinitionWithGraphsImpl _result;
    private final ConcurrentMap<ComputationTargetReference, UniqueId> _resolutions;
    private boolean _portfolioOutputs;

    protected CompilationTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
        final VersionCorrection versionCorrection, final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions) {
      s_logger.info("Compiling {} for use at {}", viewDefinition.getName(), valuationTime);
      _viewCompilationContext = new ViewCompilationContext(viewDefinition, compilationServices, valuationTime, versionCorrection);
      _resolutions = resolutions;
      if (s_logger.isDebugEnabled()) {
        new CompilationCompletionEstimate(_viewCompilationContext);
      }
      final ResultModelDefinition resultModelDefinition = viewDefinition.getResultModelDefinition();
      _portfolioOutputs = (resultModelDefinition.getPositionOutputMode() != ResultOutputMode.NONE) || (resultModelDefinition.getAggregatePositionOutputMode() != ResultOutputMode.NONE);
    }

    protected ViewCompilationContext getContext() {
      return _viewCompilationContext;
    }

    protected ConcurrentMap<ComputationTargetReference, UniqueId> getResolutions() {
      return _resolutions;
    }

    protected abstract void compile();

    private void removeUnusedResolutions(final Collection<DependencyGraph> graphs, final Portfolio portfolio) {
      final Set<UniqueId> validIdentifiers = new HashSet<UniqueId>(getResolutions().size());
      if (portfolio != null) {
        validIdentifiers.add(portfolio.getUniqueId());
      }
      // TODO: The resolution map must contain an entry for all structural nodes; just in case there are no nodes in the graph form them
      // E.g. if TRADE level values are on, then it must contain all POSITION oid->uid references regardless of whether there are position level aggregates
      for (DependencyGraph graph : graphs) {
        for (final ComputationTargetSpecification target : graph.getAllComputationTargets()) {
          validIdentifiers.add(target.getUniqueId());
        }
      }
      final Iterator<Map.Entry<ComputationTargetReference, UniqueId>> itrResolutions = getResolutions().entrySet().iterator();
      while (itrResolutions.hasNext()) {
        final Map.Entry<ComputationTargetReference, UniqueId> resolution = itrResolutions.next();
        if (resolution.getKey().getType().isTargetType(ComputationTargetType.POSITION)) {
          // Keep all positions; they'll be in our graph. It's a naughty function that could start requesting items for positions outside of the portfolio!
          continue;
        }
        if (validIdentifiers.contains(resolution.getValue())) {
          // Keep any resolutions relating to nodes in the graph
          continue;
        }
        // Delete anything else; legacy from failed resolutions
        itrResolutions.remove();
      }
    }

    /**
     * Fully resolves the portfolio structure for a view. A fully resolved structure has resolved {@link Security} objects for each {@link Position} within the portfolio. Note however that any
     * underlying or related data referenced by a security will not be resolved at this stage.
     * 
     * @param compilationContext the compilation context containing the view being compiled, not null
     * @return the resolved portfolio, not null
     */
    private Portfolio getPortfolio() {
      final UniqueId portfolioId = getContext().getViewDefinition().getPortfolioId();
      if (portfolioId == null) {
        throw new OpenGammaRuntimeException("The view definition '" + getContext().getViewDefinition().getName()
            + "' contains required portfolio outputs, but it does not reference a portfolio.");
      }
      final ComputationTargetResolver resolver = getContext().getServices().getFunctionCompilationContext().getRawComputationTargetResolver();
      final ComputationTargetResolver.AtVersionCorrection versioned = resolver.atVersionCorrection(getContext().getResolverVersionCorrection());
      final ComputationTargetSpecification specification = versioned.getSpecificationResolver()
          .getTargetSpecification(new ComputationTargetSpecification(ComputationTargetType.PORTFOLIO, portfolioId));
      if (specification == null) {
        throw new OpenGammaRuntimeException("Unable to identify portfolio '" + portfolioId + "' for view '" + getContext().getViewDefinition().getName() + "'");
      }
      final ComputationTarget target = versioned.resolve(specification);
      if (target == null) {
        throw new OpenGammaRuntimeException("Unable to resolve '" + specification + "' for view '" + getContext().getViewDefinition().getName() + "'");
      }
      return target.getValue(ComputationTargetType.PORTFOLIO);
    }

    protected boolean isPortfolioOutputs() {
      return _portfolioOutputs;
    }

    /**
     * Cancels any active builders.
     */
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
      boolean result = true;
      for (final DependencyGraphBuilder builder : getContext().getBuilders()) {
        result &= builder.cancel(mayInterruptIfRunning);
      }
      return result;
    }

    /**
     * Tests if any of the builders have been canceled.
     */
    @Override
    public boolean isCancelled() {
      boolean result = false;
      for (final DependencyGraphBuilder builder : getContext().getBuilders()) {
        result |= builder.isCancelled();
      }
      return result;
    }

    /**
     * Tests if all of the builders have completed.
     */
    @Override
    public boolean isDone() {
      return _result != null;
    }

    @Override
    public CompiledViewDefinitionWithGraphsImpl get() {
      Portfolio portfolio = null;
      for (final DependencyGraphBuilder builder : getContext().getBuilders()) {
        final FunctionCompilationContext functionContext = builder.getCompilationContext();
        final ComputationTargetResolver.AtVersionCorrection resolver = functionContext.getComputationTargetResolver();
        functionContext.setComputationTargetResolver(TargetResolutionLogger.of(resolver, getResolutions()));
        if (isPortfolioOutputs() && !functionContext.getViewCalculationConfiguration().getAllPortfolioRequirements().isEmpty()) {
          if (portfolio == null) {
            portfolio = getPortfolio();
            getResolutions().putIfAbsent(new ComputationTargetSpecification(ComputationTargetType.PORTFOLIO, getContext().getViewDefinition().getPortfolioId()), portfolio.getUniqueId());
          }
          functionContext.setPortfolio(portfolio);
        }
      }
      long t = -System.nanoTime();
      compile();
      final Collection<DependencyGraph> graphs = processDependencyGraphs(getContext());
      t += System.nanoTime();
      s_logger.info("Processed dependency graphs after {}ms", t / 1e6);
      removeUnusedResolutions(graphs, portfolio);
      _result = new CompiledViewDefinitionWithGraphsImpl(getContext().getResolverVersionCorrection(), getContext().getViewDefinition(), graphs, getResolutions(), portfolio, getContext().getServices()
          .getFunctionCompilationContext().getFunctionInitId());
      if (OUTPUT_DEPENDENCY_GRAPHS) {
        outputDependencyGraphs(graphs);
      }
      if (OUTPUT_LIVE_DATA_REQUIREMENTS) {
        outputLiveDataRequirements(graphs);
      }
      if (OUTPUT_FAILURE_REPORTS) {
        outputFailureReports(_viewCompilationContext.getBuilders());
      }
      return _result;
    }

    @Override
    public CompiledViewDefinitionWithGraphsImpl get(final long timeout, final TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

  }

  private static class FullCompilationTask extends CompilationTask {

    protected FullCompilationTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
        final VersionCorrection versionCorrection) {
      super(viewDefinition, compilationServices, valuationTime, versionCorrection, new ConcurrentHashMap<ComputationTargetReference, UniqueId>());
    }

    @Override
    protected void compile() {
      s_logger.info("Performing full compilation");
      SpecificRequirementsCompiler.execute(getContext());
      PortfolioCompiler.executeFull(getContext(), getResolutions());
    }

  }

  private abstract static class IncrementalCompilationTask extends CompilationTask {

    private final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> _previousGraphs;

    protected IncrementalCompilationTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
        final VersionCorrection versionCorrection, final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> previousGraphs,
        final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions) {
      super(viewDefinition, compilationServices, valuationTime, versionCorrection, resolutions);
      _previousGraphs = previousGraphs;
    }

    @Override
    public void compile() {
      for (final DependencyGraphBuilder builder : getContext().getBuilders()) {
        final Pair<DependencyGraph, Set<ValueRequirement>> graph = _previousGraphs.get(builder.getCalculationConfigurationName());
        if (graph != null) {
          builder.setDependencyGraph(graph.getFirst());
          if (graph.getSecond().isEmpty()) {
            s_logger.debug("No incremental work for {}", graph.getFirst());
          } else {
            s_logger.info("{} incremental resolutions required for {}", graph.getSecond().size(), graph.getFirst());
            builder.addTarget(graph.getSecond());
          }
        }
      }
    }

  }

  private static class IncrementalCompilationTaskFullResolve extends IncrementalCompilationTask {

    protected IncrementalCompilationTaskFullResolve(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
        final VersionCorrection versionCorrection, final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> previousGraphs,
        final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions) {
      super(viewDefinition, compilationServices, valuationTime, versionCorrection, previousGraphs, resolutions);
    }

    @Override
    public void compile() {
      super.compile();
      PortfolioCompiler.executeFull(getContext(), getResolutions());
    }

  }

  private static class IncrementalCompilationTaskPartialResolve extends IncrementalCompilationTask {

    private final Set<UniqueId> _changedPositions;

    protected IncrementalCompilationTaskPartialResolve(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
        final VersionCorrection versionCorrection, final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> previousGraphs,
        final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions, final Set<UniqueId> changedPositions) {
      super(viewDefinition, compilationServices, valuationTime, versionCorrection, previousGraphs, resolutions);
      _changedPositions = changedPositions;
    }

    @Override
    public void compile() {
      super.compile();
      PortfolioCompiler.executeIncremental(getContext(), getResolutions(), _changedPositions);
    }

  }

  public static Future<CompiledViewDefinitionWithGraphsImpl> fullCompileTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices, final Instant valuationTime,
      final VersionCorrection versionCorrection) {
    return new FullCompilationTask(viewDefinition, compilationServices, valuationTime, versionCorrection);
  }

  public static Future<CompiledViewDefinitionWithGraphsImpl> incrementalCompileTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices,
      final Instant valuationTime, final VersionCorrection versionCorrection, final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> previousGraphs,
      final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions, final Set<UniqueId> changedPositions) {
    return new IncrementalCompilationTaskPartialResolve(viewDefinition, compilationServices, valuationTime, versionCorrection, previousGraphs, resolutions, changedPositions);
  }

  public static Future<CompiledViewDefinitionWithGraphsImpl> incrementalCompileTask(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices,
      final Instant valuationTime, final VersionCorrection versionCorrection, final Map<String, Pair<DependencyGraph, Set<ValueRequirement>>> previousGraphs,
      final ConcurrentMap<ComputationTargetReference, UniqueId> resolutions) {
    return new IncrementalCompilationTaskFullResolve(viewDefinition, compilationServices, valuationTime, versionCorrection, previousGraphs, resolutions);
  }

  public static CompiledViewDefinitionWithGraphsImpl compile(final ViewDefinition viewDefinition, final ViewCompilationServices compilationServices,
      final Instant valuationTime, final VersionCorrection versionCorrection) {
    try {
      return fullCompileTask(viewDefinition, compilationServices, valuationTime, versionCorrection).get();
    } catch (final InterruptedException e) {
      throw new OpenGammaRuntimeException("Interrupted", e);
    } catch (final ExecutionException e) {
      throw new OpenGammaRuntimeException("Failed", e);
    }
  }

  /**
   * Tests whether the view has portfolio outputs enabled.
   * 
   * @param viewDefinition the view definition
   * @return true if there is at least one portfolio target
   */
  private static boolean isPortfolioOutputEnabled(final ViewDefinition viewDefinition) {
    final ResultModelDefinition resultModelDefinition = viewDefinition.getResultModelDefinition();
    return resultModelDefinition.getPositionOutputMode() != ResultOutputMode.NONE || resultModelDefinition.getAggregatePositionOutputMode() != ResultOutputMode.NONE;
  }

  /**
   * Returns all the dependency graphs built by the graph builders in the specified view compilation context. This method waits for their compilation to complete if necessary, and removes all
   * unnecessary values in each graph.
   * 
   * @param context the view compilation context containing the dep graph builders to query for dep graphs
   * @return the map from config names to dependency graphs
   */
  private static Collection<DependencyGraph> processDependencyGraphs(final ViewCompilationContext context) {
    final Collection<DependencyGraphBuilder> builders = context.getBuilders();
    final Collection<DependencyGraph> result = new ArrayList<DependencyGraph>(builders.size());
    for (DependencyGraphBuilder builder : builders) {
      final DependencyGraph graph = builder.getDependencyGraph();
      graph.removeUnnecessaryValues();
      result.add(graph);
      // TODO: do we want to do anything with the ValueRequirement to resolved ValueSpecification data? I don't like it being in the graph
      // as it's more specific to how the graph is used. Having it in the graph with the terminal outputs data is convenient for taking
      // sub-graphs to initialise an incremental graph builder with though.
    }
    return result;
  }

  private static void outputDependencyGraphs(final Collection<DependencyGraph> graphs) {
    final StringBuilder sb = new StringBuilder();
    for (DependencyGraph graph : graphs) {
      final String configName = graph.getCalculationConfigurationName();
      sb.append("DepGraph for ").append(configName);

      sb.append("\tProducing values ").append(graph.getOutputSpecifications());
      for (final DependencyNode depNode : graph.getDependencyNodes()) {
        sb.append("\t\tNode:\n").append(DependencyNodeFormatter.toString(depNode));
      }
    }
    s_logger.warn("Dependency Graphs -- \n{}", sb);
  }

  private static void outputLiveDataRequirements(final Collection<DependencyGraph> graphs) {
    final StringBuilder sb = new StringBuilder();
    for (DependencyGraph graph : graphs) {
      final String configName = graph.getCalculationConfigurationName();
      final Collection<ValueSpecification> requiredLiveData = graph.getAllRequiredMarketData();
      if (requiredLiveData.isEmpty()) {
        sb.append(configName).append(" requires no live data.\n");
      } else {
        sb.append("Live data for ").append(configName).append("\n");
        for (final ValueSpecification liveRequirement : requiredLiveData) {
          sb.append("\t").append(liveRequirement).append("\n");
        }
      }
    }
    s_logger.warn("Live data requirements -- \n{}", sb);
  }

  private static void outputFailureReports(final Collection<DependencyGraphBuilder> builders) {
    for (final DependencyGraphBuilder builder : builders) {
      outputFailureReport(builder);
    }
  }

  public static void outputFailureReport(final DependencyGraphBuilder builder) {
    final Map<Throwable, Integer> exceptions = builder.getExceptions();
    if (!exceptions.isEmpty()) {
      for (final Map.Entry<Throwable, Integer> entry : exceptions.entrySet()) {
        final Throwable exception = entry.getKey();
        final Integer count = entry.getValue();
        if (exception.getCause() != null) {
          if (s_logger.isDebugEnabled()) {
            s_logger.debug("Nested exception raised " + count + " time(s)", exception);
          }
        } else {
          if (s_logger.isWarnEnabled()) {
            s_logger.warn("Exception raised " + count + " time(s)", exception);
          }
        }
      }
    } else {
      s_logger.info("No exceptions raised for configuration {}", builder.getCalculationConfigurationName());
    }
  }

}

/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.client.merging;

import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.resource.EngineResourceManagerImpl;
import com.opengamma.engine.test.TestViewResultListener;
import com.opengamma.engine.value.ComputedValueResult;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.engine.view.AggregatedExecutionLog;
import com.opengamma.engine.view.ViewComputationResultModel;
import com.opengamma.engine.view.ViewDeltaResultModel;
import com.opengamma.engine.view.ViewResultEntry;
import com.opengamma.engine.view.compilation.CompiledViewDefinitionWithGraphsImpl;
import com.opengamma.engine.view.impl.InMemoryViewDeltaResultModel;
import com.opengamma.engine.view.listener.ViewResultListener;
import com.opengamma.id.UniqueId;
import com.opengamma.util.test.TestGroup;
import com.opengamma.util.test.Timeout;
import com.opengamma.lambdava.tuple.Pair;

/**
 * Tests RateLimitingMergingUpdateProvider
 */
@Test(groups = TestGroup.INTEGRATION)
public class RateLimitingMergingViewProcessListenerTest {

  private static final Logger s_logger = LoggerFactory.getLogger(RateLimitingMergingViewProcessListenerTest.class);

  @Test
  public void testPassThrough() {
    final TestViewResultListener testListener = new TestViewResultListener();
    final RateLimitingMergingViewProcessListener mergingListener = new RateLimitingMergingViewProcessListener(testListener, mock(EngineResourceManagerImpl.class), new Timer("Custom timer"));

    // OK, it doesn't really test the 'synchronous' bit, but it at least checks that no merging has happened.
    addCompile(mergingListener);
    addResults(mergingListener, 1000);
    testListener.assertViewDefinitionCompiled();
    testListener.assertMultipleCycleCompleted(1000);
    testListener.assertNoCalls();

    mergingListener.setPaused(true);
    addResults(mergingListener, 1000);
    testListener.assertNoCalls();
    mergingListener.setPaused(false);
    testListener.assertCycleCompleted();
    testListener.assertNoCalls();

    mergingListener.setPaused(false);
    addResults(mergingListener, 1000);
    testListener.assertMultipleCycleCompleted(1000);

    mergingListener.processTerminated(false);
    testListener.assertProcessTerminated();
    testListener.assertNoCalls();

    mergingListener.processTerminated(false);
  }

  @Test
  public void testMergingWhenRateLimiting() throws InterruptedException {
    final TestViewResultListener testListener = new TestViewResultListener();
    final RateLimitingMergingViewProcessListener mergingListener = new RateLimitingMergingViewProcessListener(testListener, mock(EngineResourceManagerImpl.class), new Timer("Custom timer"));
    mergingListener.setMinimumUpdatePeriodMillis(500);

    addResults(mergingListener, 1000);
    Thread.sleep(500);
    testListener.assertCycleCompleted();
    testListener.assertNoCalls();

    mergingListener.terminate();
  }

  @Test
  public void testModifiableUpdatePeriod() throws InterruptedException {
    final TestViewResultListener testListener = new TestViewResultListener();
    final RateLimitingMergingViewProcessListener mergingListener = new RateLimitingMergingViewProcessListener(testListener, mock(EngineResourceManagerImpl.class), new Timer("Custom timer"));

    assertCorrectUpdateRate(mergingListener, testListener, 100);
    assertCorrectUpdateRate(mergingListener, testListener, 400);
    assertCorrectUpdateRate(mergingListener, testListener, 50);

    mergingListener.terminate();
  }

  @Test
  public void testCallOrderingAndCollapsing() throws InterruptedException {
    final TestViewResultListener testListener = new TestViewResultListener();
    final RateLimitingMergingViewProcessListener mergingListener = new RateLimitingMergingViewProcessListener(testListener, mock(EngineResourceManagerImpl.class), new Timer("Custom timer"));

    mergingListener.setPaused(true);
    testListener.assertNoCalls();

    // Begin sequence while paused

    addCompile(mergingListener);
    addResults(mergingListener, 10);

    final CompiledViewDefinitionWithGraphsImpl preCompilation = mock(CompiledViewDefinitionWithGraphsImpl.class);
    mergingListener.viewDefinitionCompiled(preCompilation, true);

    addResults(mergingListener, 10);
    mergingListener.cycleCompleted(mock(ViewComputationResultModel.class), getDeltaResult(1));
    final ViewComputationResultModel latestResult = mock(ViewComputationResultModel.class);
    mergingListener.cycleCompleted(latestResult, getDeltaResult(2));

    final CompiledViewDefinitionWithGraphsImpl postCompilation = mock(CompiledViewDefinitionWithGraphsImpl.class);
    mergingListener.viewDefinitionCompiled(postCompilation, true);

    mergingListener.processCompleted();
    mergingListener.processTerminated(false);

    // End of sequence while paused

    mergingListener.setPaused(false);

    testListener.assertViewDefinitionCompiled(Timeout.standardTimeoutMillis());
    testListener.assertViewDefinitionCompiled(Timeout.standardTimeoutMillis(), preCompilation);
    final ViewDeltaResultModel mergedDelta = testListener.getCycleCompleted(Timeout.standardTimeoutMillis()).getDeltaResult();

    assertEquals(2, mergedDelta.getAllResults().size());
    final Set<Pair<String, Integer>> results = new HashSet<Pair<String, Integer>>();
    for (final ViewResultEntry deltaItem : mergedDelta.getAllResults()) {
      results.add(Pair.of(deltaItem.getComputedValue().getSpecification().getValueName(), (Integer) deltaItem.getComputedValue().getValue()));
    }
    assertTrue(results.contains(Pair.of("value1", 1)));
    assertTrue(results.contains(Pair.of("value2", 2)));

    testListener.assertViewDefinitionCompiled(Timeout.standardTimeoutMillis(), postCompilation);
    testListener.assertProcessCompleted();
    testListener.assertProcessTerminated();
    testListener.assertNoCalls();
  }

  private ViewDeltaResultModel getDeltaResult(final int value) {
    final InMemoryViewDeltaResultModel deltaResult = new InMemoryViewDeltaResultModel();
    deltaResult.addValue("DEFAULT", getComputedValueResult("value" + value, value));
    return deltaResult;
  }

  private ComputedValueResult getComputedValueResult(final String valueName, final Object value) {
    final ComputationTargetSpecification target = ComputationTargetSpecification.of(UniqueId.of("Scheme", valueName));
    return new ComputedValueResult(new ValueSpecification(valueName, target, ValueProperties.with(ValuePropertyNames.FUNCTION, "FunctionId").get()), value, AggregatedExecutionLog.EMPTY);
  }

  private void assertCorrectUpdateRate(final RateLimitingMergingViewProcessListener mergingListener, final TestViewResultListener testListener, final int period) throws InterruptedException {
    mergingListener.setMinimumUpdatePeriodMillis(period);
    assertUpdateRate(mergingListener, testListener, period);

    // If the provider is paused then all updates should be merged regardless of the time elapsed or the rate
    mergingListener.setPaused(true);
    for (int i = 0; i < 3; i++) {
      addResults(mergingListener, 10);
      Thread.sleep(period);
    }
    testListener.assertNoCalls();
    mergingListener.setPaused(false);
    Thread.sleep(2 * period);
    testListener.assertCycleCompleted();
    testListener.assertNoCalls();

    // Once unpaused, everything should be back to normal
    assertUpdateRate(mergingListener, testListener, period);
  }

  private void assertUpdateRate(final RateLimitingMergingViewProcessListener mergingListener, final TestViewResultListener testListener, final int period) throws InterruptedException {
    testListener.resetShortestDelay();
    for (int i = 0; i < 100; i++) {
      Thread.sleep(10);
      addResults(mergingListener, 10);
    }
    // Wait a couple of periods for any stragglers
    Thread.sleep (2 * period);
    // Check that the results didn't come any faster than we asked for (give or take 10%), and not too slowly (allow up to twice)
    assertTrue ("Expecting results no faster than " + period + " ms, but got a result after " + testListener.getShortestDelay() + " ms", testListener.getShortestDelay() >= (period - period / 10));
    assertTrue ("Expecting results no slower than " + (period * 2) + " ms, but got a result after " + testListener.getShortestDelay() + " ms", testListener.getShortestDelay() <= (period * 2));
    s_logger.info("Size = {}", testListener.getQueueSize());
    testListener.clear();
  }

  private void addResults(final ViewResultListener listener, final int count) {
    for (int i = 0; i < count; i++) {
      listener.cycleCompleted(mock(ViewComputationResultModel.class), null);
    }
  }

  private void addCompile(final ViewResultListener listener) {
    listener.viewDefinitionCompiled(mock(CompiledViewDefinitionWithGraphsImpl.class), true);
  }

}

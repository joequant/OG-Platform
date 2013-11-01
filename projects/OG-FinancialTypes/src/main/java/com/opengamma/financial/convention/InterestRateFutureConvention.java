/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.convention.ConventionType;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;

/**
 * Convention for exchange-traded interest rate futures.
 */
@BeanDefinition
public class InterestRateFutureConvention extends ExchangeTradedFutureAndOptionConvention {

  /**
   * Type of the convention.
   */
  public static final ConventionType TYPE = ConventionType.of("InterestRateFuture");

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The index convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _indexConvention;

  /**
   * Creates an instance.
   */
  protected InterestRateFutureConvention() {
    super();
  }

  /**
   * Creates an instance.
   * 
   * @param name  the convention name, not null
   * @param externalIdBundle  the external identifiers for this convention, not null
   * @param expiryConvention  the expiry convention, not null
   * @param exchangeCalendar  the exchange calendar, not null
   * @param indexConvention  the index convention, not null
   */
  public InterestRateFutureConvention(
      final String name, final ExternalIdBundle externalIdBundle, final ExternalId expiryConvention,
      final ExternalId exchangeCalendar, final ExternalId indexConvention) {
    super(name, externalIdBundle, expiryConvention, exchangeCalendar);
    setIndexConvention(indexConvention);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the type identifying this convention.
   * 
   * @return the {@link #TYPE} constant, not null
   */
  @Override
  public ConventionType getConventionType() {
    return TYPE;
  }

  /**
   * Accepts a visitor to manage traversal of the hierarchy.
   *
   * @param <T>  the result type of the visitor
   * @param visitor  the visitor, not null
   * @return the result
   */
  @Override
  public <T> T accept(final FinancialConventionVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitInterestRateFutureConvention(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code InterestRateFutureConvention}.
   * @return the meta-bean, not null
   */
  public static InterestRateFutureConvention.Meta meta() {
    return InterestRateFutureConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(InterestRateFutureConvention.Meta.INSTANCE);
  }

  @Override
  public InterestRateFutureConvention.Meta metaBean() {
    return InterestRateFutureConvention.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the index convention.
   * @return the value of the property, not null
   */
  public ExternalId getIndexConvention() {
    return _indexConvention;
  }

  /**
   * Sets the index convention.
   * @param indexConvention  the new value of the property, not null
   */
  public void setIndexConvention(ExternalId indexConvention) {
    JodaBeanUtils.notNull(indexConvention, "indexConvention");
    this._indexConvention = indexConvention;
  }

  /**
   * Gets the the {@code indexConvention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> indexConvention() {
    return metaBean().indexConvention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public InterestRateFutureConvention clone() {
    return (InterestRateFutureConvention) super.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      InterestRateFutureConvention other = (InterestRateFutureConvention) obj;
      return JodaBeanUtils.equal(getIndexConvention(), other.getIndexConvention()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getIndexConvention());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("InterestRateFutureConvention{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("indexConvention").append('=').append(JodaBeanUtils.toString(getIndexConvention())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code InterestRateFutureConvention}.
   */
  public static class Meta extends ExchangeTradedFutureAndOptionConvention.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code indexConvention} property.
     */
    private final MetaProperty<ExternalId> _indexConvention = DirectMetaProperty.ofReadWrite(
        this, "indexConvention", InterestRateFutureConvention.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "indexConvention");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          return _indexConvention;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends InterestRateFutureConvention> builder() {
      return new DirectBeanBuilder<InterestRateFutureConvention>(new InterestRateFutureConvention());
    }

    @Override
    public Class<? extends InterestRateFutureConvention> beanType() {
      return InterestRateFutureConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code indexConvention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> indexConvention() {
      return _indexConvention;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          return ((InterestRateFutureConvention) bean).getIndexConvention();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          ((InterestRateFutureConvention) bean).setIndexConvention((ExternalId) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((InterestRateFutureConvention) bean)._indexConvention, "indexConvention");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

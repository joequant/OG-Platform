/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ExternalIdBundle;
import com.opengamma.master.convention.ManageableConvention;

/**
 * An abstract base class for all conventions defined within the financial package.
 */
@BeanDefinition
public abstract class FinancialConvention extends ManageableConvention {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * For the builder.
   */
  protected FinancialConvention() {
  }

  /**
   * Creates a convention specifying the values of the main fields.
   * 
   * @param name  the name of the convention, for display purposes, not null
   * @param externalIdBundle  the bundle of identifiers that define the convention, not null
   */
  protected FinancialConvention(String name, ExternalIdBundle externalIdBundle) {
    super(name, externalIdBundle);
  }

  //-------------------------------------------------------------------------
  /**
   * Accepts a visitor to manage traversal of the hierarchy.
   *
   * @param <T>  the result type of the visitor
   * @param visitor  the visitor, not null
   * @return the result, this implementation throws IllegalStateException
   * @throws IllegalStateException This method must be overridden in subclasses to be used.
   */
  public <T> T accept(FinancialConventionVisitor<T> visitor) {
    throw new IllegalStateException("Unknown Convention type " + getClass());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FinancialConvention}.
   * @return the meta-bean, not null
   */
  public static FinancialConvention.Meta meta() {
    return FinancialConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FinancialConvention.Meta.INSTANCE);
  }

  @Override
  public FinancialConvention.Meta metaBean() {
    return FinancialConvention.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("FinancialConvention{");
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
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FinancialConvention}.
   */
  public static class Meta extends ManageableConvention.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends FinancialConvention> builder() {
      throw new UnsupportedOperationException("FinancialConvention is an abstract class");
    }

    @Override
    public Class<? extends FinancialConvention> beanType() {
      return FinancialConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

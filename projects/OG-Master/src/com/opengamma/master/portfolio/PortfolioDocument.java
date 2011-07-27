/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.portfolio;

import java.io.Serializable;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.AbstractDocument;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * A document used to pass a portfolio into and out of the portfolio master.
 * <p>
 * The portfolio consists of a tree of nodes with position identifiers.
 * To find the detail of each position, a separate search must be performed using the position master.
 */
@PublicSPI
@BeanDefinition
public class PortfolioDocument extends AbstractDocument implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   */
  @PropertyDefinition
  private UniqueIdentifier _uniqueId;
  /**
   * The portfolio.
   */
  @PropertyDefinition
  private ManageablePortfolio _portfolio;

  /**
   * Creates an instance.
   */
  public PortfolioDocument() {
  }

  /**
   * Creates an instance.
   * 
   * @param portfolioTree  the portfolio tree, not null
   */
  public PortfolioDocument(final ManageablePortfolio portfolioTree) {
    ArgumentChecker.notNull(portfolioTree, "portfolioTree");
    setUniqueId(portfolioTree.getUniqueId());
    setPortfolio(portfolioTree);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioDocument}.
   * @return the meta-bean, not null
   */
  public static PortfolioDocument.Meta meta() {
    return PortfolioDocument.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(PortfolioDocument.Meta.INSTANCE);
  }

  @Override
  public PortfolioDocument.Meta metaBean() {
    return PortfolioDocument.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
      case 1121781064:  // portfolio
        return getPortfolio();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueIdentifier) newValue);
        return;
      case 1121781064:  // portfolio
        setPortfolio((ManageablePortfolio) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      PortfolioDocument other = (PortfolioDocument) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getPortfolio(), other.getPortfolio()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPortfolio());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   * @return the value of the property
   */
  public UniqueIdentifier getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueIdentifier uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field is managed by the master but must be set for updates.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio.
   * @return the value of the property
   */
  public ManageablePortfolio getPortfolio() {
    return _portfolio;
  }

  /**
   * Sets the portfolio.
   * @param portfolio  the new value of the property
   */
  public void setPortfolio(ManageablePortfolio portfolio) {
    this._portfolio = portfolio;
  }

  /**
   * Gets the the {@code portfolio} property.
   * @return the property, not null
   */
  public final Property<ManageablePortfolio> portfolio() {
    return metaBean().portfolio().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PortfolioDocument}.
   */
  public static class Meta extends AbstractDocument.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueIdentifier> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", PortfolioDocument.class, UniqueIdentifier.class);
    /**
     * The meta-property for the {@code portfolio} property.
     */
    private final MetaProperty<ManageablePortfolio> _portfolio = DirectMetaProperty.ofReadWrite(
        this, "portfolio", PortfolioDocument.class, ManageablePortfolio.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "uniqueId",
        "portfolio");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          return _uniqueId;
        case 1121781064:  // portfolio
          return _portfolio;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends PortfolioDocument> builder() {
      return new DirectBeanBuilder<PortfolioDocument>(new PortfolioDocument());
    }

    @Override
    public Class<? extends PortfolioDocument> beanType() {
      return PortfolioDocument.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code portfolio} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageablePortfolio> portfolio() {
      return _portfolio;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

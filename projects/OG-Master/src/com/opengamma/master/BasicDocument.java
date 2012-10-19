/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master;

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

import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdentifiable;

/**
 * A minimal document used for handling operations on versions and corrections
 */
@BeanDefinition
public class BasicDocument<T extends UniqueIdentifiable> extends AbstractDocument<T> {
  
  /**
   * The document unique identifier.
   * This field is managed by the master but must be set for updates.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code BasicDocument}.
   * @param <R>  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R extends UniqueIdentifiable> BasicDocument.Meta<R> meta() {
    return BasicDocument.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(BasicDocument.Meta.INSTANCE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public BasicDocument.Meta<T> metaBean() {
    return BasicDocument.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
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
      BasicDocument<?> other = (BasicDocument<?>) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the document unique identifier.
   * This field is managed by the master but must be set for updates.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the document unique identifier.
   * This field is managed by the master but must be set for updates.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field is managed by the master but must be set for updates.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code BasicDocument}.
   */
  public static class Meta<T extends UniqueIdentifiable> extends AbstractDocument.Meta<T> {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", BasicDocument.class, UniqueId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "uniqueId");

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
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends BasicDocument<T>> builder() {
      return new DirectBeanBuilder<BasicDocument<T>>(new BasicDocument<T>());
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends BasicDocument<T>> beanType() {
      return (Class) BasicDocument.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

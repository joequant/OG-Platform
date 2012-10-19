/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.config.impl;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ObjectId;
import com.opengamma.id.ObjectIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdentifiable;


@BeanDefinition
public class ConfigItem<T> extends DirectBean implements UniqueIdentifiable, ObjectIdentifiable {

  @PropertyDefinition(validate = "notNull", set="manual")
  private T _value;

  @PropertyDefinition
  private UniqueId _uniqueId;

  @PropertyDefinition(validate = "notNull")
  private String _name;


  @Override
  public ObjectId getObjectId() {
    return _uniqueId.getObjectId();
  }

  /**
   * The type of the configuration item, not null.
   */
  @PropertyDefinition(get="manual")
  private Class<?> _type;


  /**
   *
   * @param value the underlying value of the configuration item, not null
   */
  public ConfigItem(T value) {
    _value = value;    
  }
  
  public static <T> ConfigItem<T> of(T object){
    return new ConfigItem<T>(object);
  }
  
  public static <T> ConfigItem<T> of(T object, String name){
    ConfigItem<T> configItem = new ConfigItem<T>(object);
    configItem.setName(name);
    return configItem;
  }
  
  public static <T> ConfigItem<T> of(T object, String name, Class<?> type){
    ConfigItem<T> configItem = new ConfigItem<T>(object);
    configItem.setName(name);
    configItem.setType(type);
    return configItem;
  }

  /**
   * Creates an empty item.
   * This constructor is here for automated bean construction.
   * This item is invalid until the item class gets set 
   */
  private ConfigItem() {
    _type = null;
  }

  @SuppressWarnings("unchecked")
  public Class<?> getType() {
    return _type == null ? (_value == null ? null : _value.getClass()) : _type;
  }

  void setValue(T value) {
    _value = value;
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ConfigItem}.
   * @param <R>  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> ConfigItem.Meta<R> meta() {
    return ConfigItem.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(ConfigItem.Meta.INSTANCE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ConfigItem.Meta<T> metaBean() {
    return ConfigItem.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 111972721:  // value
        return getValue();
      case -294460212:  // uniqueId
        return getUniqueId();
      case 3373707:  // name
        return getName();
      case 3575610:  // type
        return getType();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 111972721:  // value
        setValue((T) newValue);
        return;
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
        return;
      case 3373707:  // name
        setName((String) newValue);
        return;
      case 3575610:  // type
        setType((Class<?>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_value, "value");
    JodaBeanUtils.notNull(_name, "name");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ConfigItem<?> other = (ConfigItem<?>) obj;
      return JodaBeanUtils.equal(getValue(), other.getValue()) &&
          JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getType(), other.getType());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getValue());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getType());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the value.
   * @return the value of the property, not null
   */
  public T getValue() {
    return _value;
  }

  /**
   * Gets the the {@code value} property.
   * @return the property, not null
   */
  public final Property<T> value() {
    return metaBean().value().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the uniqueId.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the uniqueId.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name.
   * @return the value of the property, not null
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the name.
   * @param name  the new value of the property, not null
   */
  public void setName(String name) {
    JodaBeanUtils.notNull(name, "name");
    this._name = name;
  }

  /**
   * Gets the the {@code name} property.
   * @return the property, not null
   */
  public final Property<String> name() {
    return metaBean().name().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Sets the type of the configuration item, not null.
   * @param type  the new value of the property
   */
  public void setType(Class<?> type) {
    this._type = type;
  }

  /**
   * Gets the the {@code type} property.
   * @return the property, not null
   */
  public final Property<Class<?>> type() {
    return metaBean().type().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ConfigItem}.
   */
  public static class Meta<T> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code value} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<T> _value = (DirectMetaProperty) DirectMetaProperty.ofReadWrite(
        this, "value", ConfigItem.class, Object.class);
    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", ConfigItem.class, UniqueId.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", ConfigItem.class, String.class);
    /**
     * The meta-property for the {@code type} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Class<?>> _type = DirectMetaProperty.ofReadWrite(
        this, "type", ConfigItem.class, (Class) Class.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "value",
        "uniqueId",
        "name",
        "type");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return _value;
        case -294460212:  // uniqueId
          return _uniqueId;
        case 3373707:  // name
          return _name;
        case 3575610:  // type
          return _type;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ConfigItem<T>> builder() {
      return new DirectBeanBuilder<ConfigItem<T>>(new ConfigItem<T>());
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends ConfigItem<T>> beanType() {
      return (Class) ConfigItem.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code value} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<T> value() {
      return _value;
    }

    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Class<?>> type() {
      return _type;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

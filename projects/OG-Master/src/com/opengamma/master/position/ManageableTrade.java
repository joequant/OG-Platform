/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.master.position;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.time.Instant;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.id.Identifier;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;

/**
 * A trade forming part of a position.
 * <p>
 * A position is formed of one or more trades.
 * For example a trade of 200 shares of OpenGamma might be combined with another trade
 * of 450 shares of OpenGamma to create a combined position of 650 shares.
 */
@BeanDefinition
public class ManageableTrade extends DirectBean implements MutableUniqueIdentifiable {
  /**
   * The trade unique identifier.
   */
  @PropertyDefinition
  private UniqueIdentifier _uniqueIdentifier;
  /**
   * The position unique identifier.
   */
  @PropertyDefinition
  private UniqueIdentifier _positionId;
  /**
   * The quantity.
   */
  @PropertyDefinition
  private BigDecimal _quantity;
  /**
   * The instant trade happened.
   */
  @PropertyDefinition
  private Instant _tradeInstant;
  /**
   * The counterparty identifier.
   */
  @PropertyDefinition
  private Identifier _counterpartyId;

  /**
   * Creates an instance.
   */
  public ManageableTrade() {
  }

  /**
   * Creates a trade from trade quantity, instant and counterparty identifier.
   * @param quantity  the amount of the trade, not null
   * @param tradeInstant  the trade instant, not null
   * @param counterpartyId the counterparty identifier, not null
   */
  public ManageableTrade(BigDecimal quantity, Instant tradeInstant, Identifier counterpartyId) {
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(tradeInstant, "tradeInstant");
    ArgumentChecker.notNull(counterpartyId, "counterpartyId");
    _quantity = quantity;
    _tradeInstant = tradeInstant;
    _counterpartyId = counterpartyId;
  }
  
  /**
   * Creates a trade from uniqueIdentifier, positionId, trade quantity, instant and counterparty identifier.
   * @param uniqueIdentifier the uniqueIdentifier, not null
   * @param positionId the positionId, not null
   * @param quantity  the amount of the trade, not null
   * @param tradeInstant  the trade instant, not null
   * @param counterpartyId the counterparty identifier, not null
   */
  public ManageableTrade(UniqueIdentifier uniqueIdentifier, UniqueIdentifier positionId, BigDecimal quantity, Instant tradeInstant, Identifier counterpartyId) {
    ArgumentChecker.notNull(uniqueIdentifier, "uniqueIdentifier");
    ArgumentChecker.notNull(positionId, "positionId");
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(tradeInstant, "tradeInstant");
    ArgumentChecker.notNull(counterpartyId, "counterpartyId");
    _uniqueIdentifier = uniqueIdentifier;
    _positionId = positionId;
    _quantity = quantity;
    _tradeInstant = tradeInstant;
    _counterpartyId = counterpartyId;
  }
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ManageableTrade}.
   * @return the meta-bean, not null
   */
  public static ManageableTrade.Meta meta() {
    return ManageableTrade.Meta.INSTANCE;
  }

  @Override
  public ManageableTrade.Meta metaBean() {
    return ManageableTrade.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -125484198:  // uniqueIdentifier
        return getUniqueIdentifier();
      case 1381039140:  // positionId
        return getPositionId();
      case -1285004149:  // quantity
        return getQuantity();
      case 413348829:  // tradeInstant
        return getTradeInstant();
      case -2058077915:  // counterpartyId
        return getCounterpartyId();
    }
    return super.propertyGet(propertyName);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -125484198:  // uniqueIdentifier
        setUniqueIdentifier((UniqueIdentifier) newValue);
        return;
      case 1381039140:  // positionId
        setPositionId((UniqueIdentifier) newValue);
        return;
      case -1285004149:  // quantity
        setQuantity((BigDecimal) newValue);
        return;
      case 413348829:  // tradeInstant
        setTradeInstant((Instant) newValue);
        return;
      case -2058077915:  // counterpartyId
        setCounterpartyId((Identifier) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trade unique identifier.
   * @return the value of the property
   */
  public UniqueIdentifier getUniqueIdentifier() {
    return _uniqueIdentifier;
  }

  /**
   * Sets the trade unique identifier.
   * @param uniqueIdentifier  the new value of the property
   */
  public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
    this._uniqueIdentifier = uniqueIdentifier;
  }

  /**
   * Gets the the {@code uniqueIdentifier} property.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> uniqueIdentifier() {
    return metaBean().uniqueIdentifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the position unique identifier.
   * @return the value of the property
   */
  public UniqueIdentifier getPositionId() {
    return _positionId;
  }

  /**
   * Sets the position unique identifier.
   * @param positionId  the new value of the property
   */
  public void setPositionId(UniqueIdentifier positionId) {
    this._positionId = positionId;
  }

  /**
   * Gets the the {@code positionId} property.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> positionId() {
    return metaBean().positionId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quantity.
   * @return the value of the property
   */
  public BigDecimal getQuantity() {
    return _quantity;
  }

  /**
   * Sets the quantity.
   * @param quantity  the new value of the property
   */
  public void setQuantity(BigDecimal quantity) {
    this._quantity = quantity;
  }

  /**
   * Gets the the {@code quantity} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> quantity() {
    return metaBean().quantity().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the instant trade happened.
   * @return the value of the property
   */
  public Instant getTradeInstant() {
    return _tradeInstant;
  }

  /**
   * Sets the instant trade happened.
   * @param tradeInstant  the new value of the property
   */
  public void setTradeInstant(Instant tradeInstant) {
    this._tradeInstant = tradeInstant;
  }

  /**
   * Gets the the {@code tradeInstant} property.
   * @return the property, not null
   */
  public final Property<Instant> tradeInstant() {
    return metaBean().tradeInstant().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the counterparty identifier.
   * @return the value of the property
   */
  public Identifier getCounterpartyId() {
    return _counterpartyId;
  }

  /**
   * Sets the counterparty identifier.
   * @param counterpartyId  the new value of the property
   */
  public void setCounterpartyId(Identifier counterpartyId) {
    this._counterpartyId = counterpartyId;
  }

  /**
   * Gets the the {@code counterpartyId} property.
   * @return the property, not null
   */
  public final Property<Identifier> counterpartyId() {
    return metaBean().counterpartyId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ManageableTrade}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueIdentifier} property.
     */
    private final MetaProperty<UniqueIdentifier> _uniqueIdentifier = DirectMetaProperty.ofReadWrite(this, "uniqueIdentifier", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code positionId} property.
     */
    private final MetaProperty<UniqueIdentifier> _positionId = DirectMetaProperty.ofReadWrite(this, "positionId", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code quantity} property.
     */
    private final MetaProperty<BigDecimal> _quantity = DirectMetaProperty.ofReadWrite(this, "quantity", BigDecimal.class);
    /**
     * The meta-property for the {@code tradeInstant} property.
     */
    private final MetaProperty<Instant> _tradeInstant = DirectMetaProperty.ofReadWrite(this, "tradeInstant", Instant.class);
    /**
     * The meta-property for the {@code counterpartyId} property.
     */
    private final MetaProperty<Identifier> _counterpartyId = DirectMetaProperty.ofReadWrite(this, "counterpartyId", Identifier.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("uniqueIdentifier", _uniqueIdentifier);
      temp.put("positionId", _positionId);
      temp.put("quantity", _quantity);
      temp.put("tradeInstant", _tradeInstant);
      temp.put("counterpartyId", _counterpartyId);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public ManageableTrade createBean() {
      return new ManageableTrade();
    }

    @Override
    public Class<? extends ManageableTrade> beanType() {
      return ManageableTrade.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueIdentifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> uniqueIdentifier() {
      return _uniqueIdentifier;
    }

    /**
     * The meta-property for the {@code positionId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> positionId() {
      return _positionId;
    }

    /**
     * The meta-property for the {@code quantity} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> quantity() {
      return _quantity;
    }

    /**
     * The meta-property for the {@code tradeInstant} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Instant> tradeInstant() {
      return _tradeInstant;
    }

    /**
     * The meta-property for the {@code counterpartyId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Identifier> counterpartyId() {
      return _counterpartyId;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

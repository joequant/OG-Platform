/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.examples.simulated.component;

import java.util.LinkedHashMap;

import py4j.GatewayServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.joda.beans.BeanDefinition;
import org.joda.beans.PropertyDefinition;

import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;

/**
 * Example tool to connect to python
 */
@BeanDefinition
public class Py4jConnectionFactory extends AbstractComponentFactory {
   /** the logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(Py4jConnectionFactory.class);

   /**
     * The flag indicating if the component is active.
     * This can be used from configuration to disable the Jetty server.
     * True by default.
     */
  @PropertyDefinition
  private boolean _active = true;

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, 
        LinkedHashMap<String, String> configuration) 
    throws Exception {
    if (isActive() == false) {
      return;
    }
    GatewayServer server = new GatewayServer(this);
    server.start();
    LOGGER.info("started GatewayServer");
  }

    //-----------------------------------------------------------------------     
    /**                                                                           
     * Gets the flag indicating if the component is active.                                 
     * True by default.                                                           
     * @return the value of the property                                          
     */
  public boolean isActive() {
    return _active;
  }

    /**                                                                           
     * Sets the flag indicating if the component is active.                                 
     * True by default.                                                           
     * @param active  the new value of the property                               
     */
  public void setActive(boolean active) {
    this._active = active;
  }
}

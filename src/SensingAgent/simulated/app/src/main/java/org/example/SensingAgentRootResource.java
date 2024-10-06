package org.example;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SensingAgentRootResource extends CoapResource {
  public SensingAgentRootResource() {
    super("");
  }

  @Override
  public void handlePOST(CoapExchange exchange) {
    exchange.respond("Hello, Sensing Agent!");
  }

  @Override
  public void handleGET(CoapExchange exchange) {
    exchange.respond("Hello, Sensing Agent!");
  }
}

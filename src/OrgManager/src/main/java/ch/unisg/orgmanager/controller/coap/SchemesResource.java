package ch.unisg.orgmanager.controller.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SchemesResource extends CoapResource {

    public SchemesResource(String name) {
        super(name);
    }

    /*
    * Get schemes
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        // TODO: Implement function to retrieve schemes
    }

    /*
    * Create a new scheme
     */
    @Override
    public void handlePOST(CoapExchange exchange) {
        // TODO: Create a new scheme and add another resource
    }
}

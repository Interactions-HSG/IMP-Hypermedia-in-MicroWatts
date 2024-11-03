package ch.unisg.omi.controller.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SchemeResource extends CoapResource {

    public SchemeResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        // TODO: Implement coap endpoint to retrieve information for a specific scheme by id
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

    }

}

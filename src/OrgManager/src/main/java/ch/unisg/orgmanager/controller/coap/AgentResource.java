package ch.unisg.orgmanager.controller.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class AgentResource extends CoapResource {

    public AgentResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        Request request = exchange.advanced().getRequest();

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Hello World!");

        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        // TODO: Add agent to agents of the OrgManager
    }
}

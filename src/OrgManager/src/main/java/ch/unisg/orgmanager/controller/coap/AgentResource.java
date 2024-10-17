package ch.unisg.orgmanager.controller.coap;

import ch.unisg.orgmanager.core.entity.Organization;
import ch.unisg.orgmanager.core.port.in.AgentUseCase;
import ch.unisg.orgmanager.core.service.AgentService;
import moise.oe.OEAgent;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

public class AgentResource extends CoapResource {

    private final AgentUseCase agentUseCase;

    public AgentResource(String name) {
        super(name);
        this.agentUseCase = new AgentService();
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        Request request = exchange.advanced().getRequest();

        List<OEAgent> agents = agentUseCase.getAgents();

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload(agents.toString());

        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        Request request = exchange.advanced().getRequest();

        agentUseCase.addAgent(request.getPayloadString());

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Agent is added.");
        exchange.respond(response);
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        /*
        Before the sensing agent dies, it sends a request.

         */
        Request request = exchange.advanced().getRequest();

        agentUseCase.removeAgent(request.getPayloadString());

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Agent is removed.");
        exchange.respond(response);
    }
}

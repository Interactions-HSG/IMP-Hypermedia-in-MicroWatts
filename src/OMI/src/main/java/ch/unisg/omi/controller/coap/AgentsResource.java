package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.core.port.in.AgentUseCase;
import ch.unisg.omi.core.service.AgentService;
import moise.oe.OEAgent;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import java.util.List;

public class AgentsResource extends CoapResource {

    private final AgentUseCase agentUseCase;
    private final CoapServerConfig server;

    public AgentsResource(String name) {
        super(name);
        this.agentUseCase = new AgentService();
        this.server = CoapServerConfig.getInstance();
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
        /*
            Add agent to agent list
         */

        // retrieve the request
        Request request = exchange.advanced().getRequest();

        // add agent to the organization
        agentUseCase.addAgent(request.getPayloadString());

        // create a new resource endpoint for the agent
        Resource roles = server.getRoot().getChild("agents");
        roles.add(new AgentResource(request.getPayloadString()));
        Response response = new Response(CoAP.ResponseCode.CREATED);

        // send a response to the client
        response.setPayload("Agent is added.");
        exchange.respond(response);
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        /*
        * Before the sensing agent dies, it sends a request.
         */
        Request request = exchange.advanced().getRequest();

        agentUseCase.removeAgent(request.getPayloadString());

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Agent is removed.");
        exchange.respond(response);
    }
}
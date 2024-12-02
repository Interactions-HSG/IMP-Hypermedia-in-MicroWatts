package ch.unisg.omi.controller.coap;

import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.service.GroupService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class GroupsResource extends CoapResource {

    private final GroupUseCase groupUseCase;

    public GroupsResource(String name) {
        super(name);
        this.groupUseCase = new GroupService();
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        /*
         * Get all group ids
         */
        Request request = exchange.advanced().getRequest();

        Response response = new Response(CoAP.ResponseCode.CONTENT);

        groupUseCase.getGroups();

        response.setPayload(groupUseCase.getGroups());

        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        /*
         * Join group
         */

        Request request = exchange.advanced().getRequest();

        groupUseCase.addGroup(request.getPayloadString());

        Response response = new Response(CoAP.ResponseCode.CREATED);

        exchange.respond(response);
    }
}

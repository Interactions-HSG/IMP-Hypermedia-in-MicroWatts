package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.service.GroupService;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

/*
 * TODO: Update group resource if group is well-formed.
 */
public class GroupResource extends CoapResource {

    private final GroupUseCase groupUseCase;

    public GroupResource(String name) {
        super(name);
        this.groupUseCase = new GroupService();
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        Request request = exchange.advanced().getRequest();

        Response response = new Response(CoAP.ResponseCode.CONTENT);

        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        /*
         * TODO: Adopt a role?
         */

        Request request = exchange.advanced().getRequest();

        Gson gson = new Gson();

        Response response = exchange.advanced().getResponse();

        exchange.respond(response);

    }

}

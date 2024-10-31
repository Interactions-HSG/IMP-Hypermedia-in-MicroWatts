package ch.unisg.orgmanager.controller.coap;

import ch.unisg.orgmanager.config.CoapServerConfig;
import ch.unisg.orgmanager.controller.dto.RoleDTO;
import ch.unisg.orgmanager.core.port.in.RoleUseCase;
import ch.unisg.orgmanager.core.port.in.command.RoleCommand;
import ch.unisg.orgmanager.core.service.RoleService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RoleResource extends CoapResource {

    private final RoleUseCase roleUseCase;

    public RoleResource(String name) {
        super(name);
        this.roleUseCase = new RoleService();
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
        /*
            Agent adopts a role
         */

        Request request = exchange.advanced().getRequest();

        Gson gson = new Gson();
        RoleDTO roleDTO = gson.fromJson(request.getPayloadString(), RoleDTO.class);

        RoleCommand command = new RoleCommand(roleDTO.getAgentName(), roleDTO.getRoleName(), roleDTO.getGroupName());

        roleUseCase.adoptRole(command);

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Role adopted.");
        exchange.respond(response);
    }
}

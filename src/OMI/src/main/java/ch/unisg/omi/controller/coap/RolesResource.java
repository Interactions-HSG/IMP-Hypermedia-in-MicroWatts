package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import ch.unisg.omi.core.service.RoleService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

public class RolesResource extends CoapResource {

    private final RoleUseCase roleUseCase;
    private final CoapServerConfig server;

    public RolesResource(String name) {
        super(name);
        this.roleUseCase = new RoleService();
        this.server = CoapServerConfig.getInstance();
    }

    /*
     *
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        // TODO: Return list of roles
        Request request = exchange.advanced().getRequest();

        Response response = new Response(CoAP.ResponseCode.CONTENT);
        // response.setPayload(roles);

        exchange.respond(response);
    }
}

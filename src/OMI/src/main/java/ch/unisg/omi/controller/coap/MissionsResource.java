package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.service.MissionService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

public class MissionsResource extends CoapResource {

    private final CoapServerConfig server;
    private final MissionUseCase missionUseCase;

    public MissionsResource(String name) {
        super(name);
        this.server = CoapServerConfig.getInstance();
        this.missionUseCase = new MissionService();
    }

    @Override
    public void handleGET(CoapExchange exchange) {}

    @Override
    public void handlePOST(CoapExchange exchange) {
        /*
            Add mission as resource
         */
        Request request = exchange.advanced().getRequest();

        Resource missions = server.getRoot().getChild("missions");
        missions.add(new MissionResource(request.getPayloadString()));

        Response response = new Response(CoAP.ResponseCode.CREATED);

        response.setPayload("Mission is added.");
    }

}

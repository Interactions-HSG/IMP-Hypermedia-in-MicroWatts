package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.AgentUseCase;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.service.AgentService;
import ch.unisg.omi.core.service.MissionService;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class AgentResource extends CoapResource {

    private final AgentUseCase agentUseCase;
    private final MissionUseCase missionUseCase;

    public AgentResource(String name) {
        super(name);
        this.agentUseCase = new AgentService();
        this.missionUseCase = new MissionService();
    }

    @Override
    public void handleGET(CoapExchange exchange) {

    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        /*
            Commit to a mission
         */

        Request request = exchange.advanced().getRequest();

        Gson gson = new Gson();
        MissionDTO missionDTO = gson.fromJson(request.getPayloadString(), MissionDTO.class);

        MissionCommand command = new MissionCommand(
                missionDTO.getAgentName(),
                missionDTO.getMissionName(),
                missionDTO.getSchemeName()
        );

        missionUseCase.commitMission(command);

        Response response = exchange.advanced().getResponse();

        exchange.respond(response);

    }
}

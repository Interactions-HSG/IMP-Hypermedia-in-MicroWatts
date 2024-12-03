package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.service.MissionService;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class MissionResource extends CoapResource {

    private final MissionUseCase missionUseCase;

    public MissionResource(String name) {
        super(name);
        this.missionUseCase = new MissionService();
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
                missionDTO.getAgentId(),
                missionDTO.getMissionId(),
                missionDTO.getSchemeId(),
                missionDTO.getGoalId()
        );

        missionUseCase.commitMission(command);

        Response response = exchange.advanced().getResponse();

        exchange.respond(response);
    }
}

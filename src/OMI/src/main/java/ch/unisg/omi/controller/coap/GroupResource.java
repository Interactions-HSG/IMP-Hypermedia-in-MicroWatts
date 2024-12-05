package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.controller.dto.RoleDTO;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ObservableResource;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupResource extends CoapResource implements ObservableResource {


    private ArrayList<HashMap<String, String>> goals = new ArrayList<>();

    private final GroupUseCase groupUseCase;
    private final RoleUseCase roleUseCase;
    private final MissionUseCase missionUseCase;

    public void addGoal(HashMap<String, String> goal) {
        goals.add(goal);
    }

    public void notifyResource() {
        this.notifyObserverRelations(null);
    }

    public GroupResource(String name, GroupUseCase groupUseCase, RoleUseCase roleUseCase, MissionUseCase missionUseCase) {
        super(name);
        this.groupUseCase = groupUseCase;
        this.roleUseCase = roleUseCase;
        this.missionUseCase = missionUseCase;
    }

    /*
     * Get a list of all goals
     */
    @Override
    public void handleGET(CoapExchange exchange) {

        System.out.println(this.goals);

        this.goals.forEach(goal -> {
            System.out.println(goal);
        });
        exchange.respond(CoAP.ResponseCode.CONTENT, this.goals.toString());
    }

    /*
     * Adopt a role
     */
    @Override
    public void handlePOST(CoapExchange exchange) {

        try {

            Gson gson = new Gson();

            RoleDTO roleDTO = gson.fromJson(exchange.getRequestText(), RoleDTO.class);

            RoleCommand command = new RoleCommand(
                    roleDTO.getAgentId(),
                    roleDTO.getRoleId(),
                    roleDTO.getGroupId()
            );

            roleUseCase.adoptRole(command);

            exchange.respond(CoAP.ResponseCode.CONTINUE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }
    }

    /*
     * Commit to a mission
     */
    @Override
    public void handlePUT(CoapExchange exchange) {

        try {

            Gson gson = new Gson();

            MissionDTO missionDTO = gson.fromJson(exchange.getRequestText(), MissionDTO.class);

            MissionCommand command = new MissionCommand(
                    missionDTO.getAgentId(),
                    missionDTO.getMissionId(),
                    missionDTO.getSchemeId(),
                    missionDTO.getGoalId()
            );

            missionUseCase.commitMission(command);

            exchange.respond(CoAP.ResponseCode.CONTINUE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }
    }
}

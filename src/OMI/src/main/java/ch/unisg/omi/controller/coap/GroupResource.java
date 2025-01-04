package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.controller.dto.RoleDTO;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Setter;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ObservableResource;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupResource extends CoapResource implements ObservableResource {


    private ArrayList<HashMap<String, String>> goals = new ArrayList<>();

    @Setter
    private boolean wellFormed;

    private final GroupUseCase groupUseCase;
    private final RoleUseCase roleUseCase;
    private final MissionUseCase missionUseCase;

    public void addGoal(HashMap<String, String> goal) {
        // check if the goal already exists in the list
        // goals are the same if goalId, missionId and schemeId are the same
        for (HashMap<String, String> existingGoal : goals) {
            if (existingGoal.get("goalId").equals(goal.get("goalId")) &&
                existingGoal.get("missionId").equals(goal.get("missionId")) &&
                existingGoal.get("schemeId").equals(goal.get("schemeId"))) {
                // Goal already exists, do not add it again
                return;
            }
        }
        goals.add(goal);
    }

    public void notifyResource(final boolean groupWellFormed) {
        System.out.println("Resource has changed - Sending Notification");
        setWellFormed(groupWellFormed);
        this.changed();
        //this.notifyObserverRelations(null);
    }

    public GroupResource(String name, GroupUseCase groupUseCase, RoleUseCase roleUseCase, MissionUseCase missionUseCase) {
        super(name, true);
        this.wellFormed = false;
        System.out.println("[GroupResource] Resource created with name: " + name);
        this.setObservable(true);
        this.groupUseCase = groupUseCase;
        this.roleUseCase = roleUseCase;
        this.missionUseCase = missionUseCase;
    }

  /*
     * Get a list of all goals
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        System.out.println("[GroupResource] GET request received");

        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();

        if (wellFormed) {
            jsonResponse.addProperty("status", "well-formed");
            jsonResponse.add("goals", gson.toJsonTree(this.goals));
        } else {
            jsonResponse.addProperty("status","not well-formed");
            jsonResponse.add("goals", gson.toJsonTree(new ArrayList<>()));
        }
        System.out.println("[GroupResource] Response: " + jsonResponse);
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonResponse.toString());
    }

    /*
     * Adopt a role
     */
    @Override
    public void handlePOST(CoapExchange exchange) {
        System.out.println("[GroupResource] POST request received");
        try {

            Gson gson = new Gson();

            RoleDTO roleDTO = gson.fromJson(exchange.getRequestText(), RoleDTO.class);

            RoleCommand command = new RoleCommand(
                    roleDTO.getAgentId(),
                    roleDTO.getRoleId(),
                    roleDTO.getGroupId()
            );

            int res = roleUseCase.adoptRole(command);

            if (res == 1) {
                System.out.println("[GroupResource] Role already occupied: " + roleDTO.getRoleId());
                exchange.respond(CoAP.ResponseCode.CONFLICT);
            } else {
                System.out.println("[GroupResource] Sucessfully adopted Role: " + roleDTO.getRoleId());
                exchange.respond(CoAP.ResponseCode.CREATED);
            }
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
        System.out.println("[GroupResource] PUT request received");
        try {

            Gson gson = new Gson();

            MissionDTO missionDTO = gson.fromJson(exchange.getRequestText(), MissionDTO.class);

            MissionCommand command = new MissionCommand(
                    missionDTO.getAgentId(),
                    missionDTO.getMissionId(),
                    missionDTO.getSchemeId(),
                    missionDTO.getGoalId()
            );


            System.out.println("[GroupResource] Mission command: " + command);
            missionUseCase.commitMission(command);

            exchange.respond(CoAP.ResponseCode.CONTINUE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }
    }
}

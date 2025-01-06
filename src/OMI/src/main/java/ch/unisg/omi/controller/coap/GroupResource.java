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
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ObservableResource;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupResource extends CoapResource implements ObservableResource {


    private static final Logger LOGGER = LoggerFactory.getLogger(GroupResource.class);

    private ArrayList<HashMap<String, String>> goals = new ArrayList<>();

    private final HashMap<String, ObserveRelation> observers;

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
        LOGGER.info("Resource has changed - Sending Notification");
        setWellFormed(groupWellFormed);
        this.changed();
    }

    public GroupResource(String name, GroupUseCase groupUseCase, RoleUseCase roleUseCase, MissionUseCase missionUseCase) {
        super(name, true);
        this.wellFormed = false;
        LOGGER.info("Resource created with name: {}", name);
        this.setObservable(true);
        this.groupUseCase = groupUseCase;
        this.roleUseCase = roleUseCase;
        this.missionUseCase = missionUseCase;
        this.observers = new HashMap<>();
    }

  /*
     * Get a list of all goals
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        LOGGER.info("GET request received");
        exchange.accept();

        final var observeRelation = exchange.advanced().getRelation();

        if (observeRelation == null) {
            LOGGER.info("Observe relation is null");
        } else {
            LOGGER.info("Observe relation: {}", observeRelation);
            final var agentId = exchange.getRequestText();
            LOGGER.info("Agent ID: {}", agentId);
            if (!observers.containsKey(agentId)) {
                LOGGER.info("Adding observer relation for agent: {}", agentId);
                observers.put(agentId, observeRelation);
            }
        }

        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();

        if (wellFormed) {
            jsonResponse.addProperty("status", "well-formed");
            jsonResponse.add("goals", gson.toJsonTree(this.goals));
        } else {
            jsonResponse.addProperty("status","not well-formed");
            jsonResponse.add("goals", gson.toJsonTree(new ArrayList<>()));
        }
        LOGGER.info("Response: {}", jsonResponse);
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonResponse.toString());
    }

    /*
     * Adopt a role
     */
    @Override
    public void handlePOST(CoapExchange exchange) {
        LOGGER.info("POST request received");
        try {

            Gson gson = new Gson();

            RoleDTO roleDTO = gson.fromJson(exchange.getRequestText(), RoleDTO.class);

            RoleCommand command = new RoleCommand(
                    roleDTO.getAgentId(),
                    roleDTO.getRoleId(),
                    roleDTO.getGroupId()
            );
            LOGGER.info("Agent: {}, Role: {}, Group: {}", command.getAgentId(), command.getRoleId(), command.getGroupId());

            int res = roleUseCase.adoptRole(command);

            if (res == 1) {
                LOGGER.info("Role already occupied: {}", roleDTO.getRoleId());
                exchange.respond(CoAP.ResponseCode.CONFLICT);
            } else {
                LOGGER.info("Sucessfully adopted Role: {}", roleDTO.getRoleId());
                exchange.respond(CoAP.ResponseCode.CREATED);
            }
        } catch (Exception e) {
            LOGGER.error("Error: {}", e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }
    }

    /*
     * Commit to a mission
     */
    @Override
    public void handlePUT(CoapExchange exchange) {
        LOGGER.info("PUT request received");
        try {

            Gson gson = new Gson();

            MissionDTO missionDTO = gson.fromJson(exchange.getRequestText(), MissionDTO.class);

            MissionCommand command = new MissionCommand(
                    missionDTO.getAgentId(),
                    missionDTO.getMissionId(),
                    missionDTO.getSchemeId(),
                    missionDTO.getGoalId()
            );


            LOGGER.info("Mission command: {}", command);
            missionUseCase.commitMission(command);

            exchange.respond(CoAP.ResponseCode.CONTINUE);

        } catch (Exception e) {
            LOGGER.error("Error: {}", e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }
    }

    public void removeObserverRelation(String agentId) {
        LOGGER.info("Removing observer relation for agent: {}", agentId);
        final var relation = observers.get(agentId);
        if (relation != null) {
            this.removeObserveRelation(relation);
            observers.remove(agentId);
        }
    }
}

package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import ch.unisg.ics.interactions.wot.td.schemas.ObjectSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.controller.coap.GroupResource;
import ch.unisg.omi.core.entity.Broadcaster;
import ch.unisg.omi.core.port.out.AgentPort;
import moise.oe.OEAgent;
import moise.oe.SchemeInstance;
import moise.os.fs.Goal;
import moise.os.fs.Mission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Primary
@Component
public class AgentAdapter implements AgentPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentAdapter.class);


    private final CoapServerConfig server = CoapServerConfig.getInstance();

    @Override
    public void sendGroupName(String agentId, String groupName) {

        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, agentId);

            Optional<ActionAffordance> action = td.getActionByName("advertiseMessage");

            if (action.isPresent()) {

                TDHttpRequest request = new TDHttpRequest(
                        action.get().getFirstForm().orElseThrow(),
                        TD.invokeAction
                );

                request.addHeader("Slug", "omi");
                request.addHeader("X-Agent-WebID", "http://omi:7500/workspaces/root/artifacts/omi");

                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                if (inputSchema.isPresent()) {

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("performative", "tell");
                    payload.put("sender", "omi");
                    payload.put("receiver", "bob");
                    payload.put("content", String.format("group(%s)", groupName));
                    payload.put("msgId", "1");

                    request.setObjectPayload((ObjectSchema) inputSchema.get(), payload);

                    TDHttpResponse response = request.execute();

                    LOGGER.info("Status code: " + response.getStatusCode());

                } else {
                    LOGGER.info("Input schema not found.");
                }
            } else {
                LOGGER.info("Action not found.");
            }

        } catch (Exception e) {
            LOGGER.error("Error sending group name", e);
        }
    }

    @Override
    public void sendRoles(String agentId, Object[] roles) {

        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, agentId);

            Optional<ActionAffordance> action = td.getActionByName("advertiseMessage");

            if (action.isPresent()) {

                TDHttpRequest request = new TDHttpRequest(
                        action.get().getFirstForm().orElseThrow(),
                        TD.invokeAction
                );

                request.addHeader("Slug", "omi");
                request.addHeader("X-Agent-WebID", "http://omi:7500/workspaces/root/artifacts/omi");

                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                if (inputSchema.isPresent()) {

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("performative", "tell");
                    payload.put("sender", "omi");
                    payload.put("receiver", "bob");
                    payload.put("content", String.format("roles(%s)", Arrays.toString(roles)));
                    payload.put("msgId", "2");

                    request.setObjectPayload((ObjectSchema) inputSchema.get(), payload);

                    TDHttpResponse response = request.execute();

                    LOGGER.info("Status code: " + response.getStatusCode());
                } else {
                    LOGGER.info("Input schema not found.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending roles", e);
        }
    }

    @Override
    public void sendGoal(OEAgent agentId, Goal goal, String groupId, Mission mission, SchemeInstance scheme) {

        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, agentId.toString());

            Optional<ActionAffordance> action = td.getActionByName("advertiseMessage");

            if (action.isPresent()) {
                TDHttpRequest request = new TDHttpRequest(
                        action.get().getFirstForm().orElseThrow(),
                        TD.invokeAction
                );

                request.addHeader("Slug", "omi");
                request.addHeader("X-Agent-WebID", "http://omi:7500/workspaces/root/artifacts/omi");

                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                if (inputSchema.isPresent()) {

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("performative", "achieve");
                    payload.put("sender", "omi");
                    payload.put("receiver", "bob");
                    payload.put("content", String.format("%s(%s,%s)",goal, mission.getId(), scheme.getId()));
                    payload.put("msgId", "3");

                    request.setObjectPayload((ObjectSchema) inputSchema.get(), payload);

                    TDHttpResponse response = request.execute();

                    LOGGER.info("Status code: " + response.getStatusCode());
                } else {
                    LOGGER.info("Input schema not found.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending goal", e);
        }
    }

    @Override
    public void notifyGoal(List<Broadcaster.PlayerInfo> playerInfos) {
        var group = (GroupResource) server.getRoot().getChild(playerInfos.getFirst().groupId());

        playerInfos.forEach(playerInfo -> {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("goalId", playerInfo.goal().getId());
            hashMap.put("missionId", playerInfo.mission().getId());
            hashMap.put("schemeId", playerInfo.scheme().getId());

            group.addGoal(hashMap);
        });

        group.notifyResource(true);
    }

    @Override
    public void notifyGroup(OEAgent agent, String groupId) {
        LOGGER.info("notifyGroup: " + groupId);

        /* Update group resource to notify CoAP clients */
        var group = (GroupResource) server.getRoot().getChild(groupId);
        group.notifyResource(false);

        /* Notify */

        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, agent.getId());

            Optional<ActionAffordance> action = td.getActionByName("advertiseMessage");

            if (action.isPresent()) {
                TDHttpRequest request = new TDHttpRequest(
                        action.get().getFirstForm().orElseThrow(),
                        TD.invokeAction
                );

                request.addHeader("Slug", "omi");
                request.addHeader("X-Agent-WebID", "http://omi:7500/workspaces/root/artifacts/omi");

                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                if (inputSchema.isPresent()) {

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("performative", "achieve");
                    payload.put("sender", "omi");
                    payload.put("receiver", "bob");
                    payload.put("content", String.format("%s(%b)", "notifyGroup", false));
                    payload.put("msgId", "3");

                    request.setObjectPayload((ObjectSchema) inputSchema.get(), payload);

                    TDHttpResponse response = request.execute();

                    LOGGER.info("Status code: " + response.getStatusCode());
                } else {
                    LOGGER.info("Input schema not found.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending goal", e);
        }

    }

    @Override
    public void removeObserverRelation(String agentId, String groupId) {
        var group = (GroupResource) server.getRoot().getChild(groupId);
        group.removeObserverRelation(agentId);
    }
}

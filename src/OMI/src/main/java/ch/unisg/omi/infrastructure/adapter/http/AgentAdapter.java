package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.ArraySchema;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import ch.unisg.ics.interactions.wot.td.schemas.ObjectSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.controller.coap.GroupResource;
import ch.unisg.omi.core.port.out.AgentPort;
import com.google.gson.Gson;
import moise.oe.OEAgent;
import moise.oe.SchemeInstance;
import moise.os.fs.Goal;
import moise.os.fs.Mission;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Primary
@Component
public class AgentAdapter implements AgentPort {


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

                    System.out.println("Status code: " + response.getStatusCode());

                } else {

                    System.out.println("Input schema not found.");
                }
            } else {
                System.out.println("Action not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
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

                    System.out.println("Status code: " + response.getStatusCode());

                } else {

                    System.out.println("Input schema not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

                    System.out.println("Status code: " + response.getStatusCode());

                } else {

                    System.out.println("Input schema not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifyGoal(OEAgent agentId, Goal goal, String groupId, Mission mission, SchemeInstance schemeInstance) {

        var group = (GroupResource) server.getRoot().getChild(groupId);

        System.out.println("Adapter: " + goal.getId() + mission.getId() + schemeInstance.getId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("goalId", goal.getId());
        hashMap.put("missionId", mission.getId());
        hashMap.put("schemeId", schemeInstance.getId());

        group.addGoal(hashMap);

        group.notifyResource();
    }
}

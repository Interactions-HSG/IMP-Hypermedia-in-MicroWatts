package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import ch.unisg.ics.interactions.wot.td.schemas.ObjectSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import ch.unisg.omi.core.port.out.AgentPort;
import com.google.gson.Gson;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Primary
@Component
public class AgentAdapter implements AgentPort {

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
}

package ch.unisg.omi.config;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.StringSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

public class YggdrasilConfig {

    private static Environment env;

    public static String ENTRYPOINT;
    public static String BASE;
    public static String WORKSPACE = "root";
    public static String NAME = "omi";

    private YggdrasilConfig(Environment env) {
        YggdrasilConfig.env = env;
        initialize();
    }

    /*
     * Set up yggdrasil
     */
    private void initialize() {
        ENTRYPOINT = env.getProperty("ENTRYPOINT", "http://127.0.0.1:8080/");
        BASE = env.getProperty("BASE", "http://127.0.0.1:7500/");

        try {
            String url = ENTRYPOINT + "workspaces/" + WORKSPACE;

            ThingDescription tdWorkspace = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, url);

            Optional<ActionAffordance> action = tdWorkspace.getActionByName("createArtifact");

            if (action.isPresent()) {

                TDHttpRequest requestArtifact = new TDHttpRequest(action.get().getFirstForm()
                        .orElseThrow(),
                        TD.invokeAction);
                requestArtifact.addHeader("Slug", NAME);
                requestArtifact.addHeader("X-Agent-WebID", BASE);

                final var metadata = Files.readString(Path.of("metadata.ttl"));

                StringSchema schema = new StringSchema.Builder().build();
                requestArtifact.setPrimitivePayload(schema, metadata);

                TDHttpResponse responseArtifact = requestArtifact.execute();

                System.out.println("Status code: " + responseArtifact.getStatusCode());
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static YggdrasilConfig instance;

    public static YggdrasilConfig getInstance(Environment env) {
        if (instance == null) {
            instance = new YggdrasilConfig(env);
        }
        return instance;
    }
    /*
     * Subscribe to a topic
     */
    public void subscribe(String topic, String callback) {

        HashMap map = new HashMap<>();
        map.put("hub.topic", topic);
        map.put("hub.callback", callback);
        map.put("hub.mode", "subscribe");

        Gson gson = new Gson();
        String body = gson.toJson(map);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ENTRYPOINT + "hub/"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

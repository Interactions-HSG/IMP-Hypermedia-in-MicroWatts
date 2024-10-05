package ch.unisg.orgmanager.config;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.StringSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class YggdrasilConfig {

    public static String ENTRYPOINT = "http://yggdrasil:8080/";
    public static String WORKSPACE = "root";
    public static String NAME = "org-manager";

    public YggdrasilConfig() {
        try {
            // ThingDescription tdYggdrasil = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT);

            ThingDescription tdWorkspace = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT + "workspaces/" + WORKSPACE);

            Optional<ActionAffordance> action = tdWorkspace.getActionByName("createArtifact");

            if (action.isPresent()) {
                
                TDHttpRequest requestArtifact = new TDHttpRequest(action.get().getFirstForm()
                        .orElseThrow(),
                        TD.invokeAction);
                requestArtifact.addHeader("Slug", NAME);
                requestArtifact.addHeader("X-Agent-WebID", "http://orgmanager:7500/");

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
}

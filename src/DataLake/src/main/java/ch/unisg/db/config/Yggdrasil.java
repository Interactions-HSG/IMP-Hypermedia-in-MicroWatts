package ch.unisg.db.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Yggdrasil {

    public static String ENTRYPOINT = "http://yggdrasil:8080/";
    public static String WORKSPACE = "root";
    public static String name = "datalake";

    public static void main() throws IOException {
        /**

        // read the thing description of yggdrasil
        ThingDescription tdYggdrasil = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT);


        // read the thing description of the workspace
        ThingDescription tdWorkspace =
            TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT +
                "workspaces/" + WORKSPACE);

        // retrieve the action to create a new artifact in order to interact with the service itself
        Optional<ActionAffordance> action = tdWorkspace.getActionByName("createArtifact");


        // if the action is valid, create a new artifact
        if (action.isPresent()) {

            // create a new form because the default form only allows Content-Type: application/json
            Form form = new Form.Builder("http://yggdrasil:8080/workspaces/db/artifacts/")
                    .setMethodName("POST")
                    .setContentType("text/turtle")
                    .addOperationType(TD.invokeAction)
                    .build();

            // create a request
            TDHttpRequest requestArtifact = new TDHttpRequest(action.get().getFirstForm()
                .orElseThrow(),
                TD.invokeAction);
            requestArtifact.addHeader("Slug", name);
            requestArtifact.addHeader("X-Agent-WebID", "http://db:7600/");

            final var metadata = Files.readString(Path.of("metadata.ttl"));
            // add td to the body
            StringSchema schema = new StringSchema.Builder().build();
            requestArtifact.setPrimitivePayload(schema, metadata);

            TDHttpResponse responseArtifact = requestArtifact.execute();

            System.out.println("Status code: " + responseArtifact.getStatusCode());
        } else {
            System.out.println("No action found");
        }
        */
    }
}

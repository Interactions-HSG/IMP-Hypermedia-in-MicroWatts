package ch.unisg.db;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.io.TDGraphWriter;
import ch.unisg.ics.interactions.wot.td.schemas.StringSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;

import java.io.IOException;
import java.util.Optional;

public class Launcher {

    public static String ENTRYPOINT = "http://yggdrasil:8080/";
    public static String name = "db";
    public static String telemetry = "telemetry";

    public static void main() throws IOException {

        System.out.println("Starting server...");

        /**
         * read the thing description of yggdrasil
         * retrieve the action to create a workspace
         * if the action exists, create a new workspace
         */

        // read the thing description of yggdrasil
        ThingDescription tdYggdrasil = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT);

        // retrieve action to create a new workspace for the db
        Optional<ActionAffordance> createWorkspace = tdYggdrasil.getActionByName("createWorkspace");

        // if the action is valid, create a new workspace
        if (createWorkspace.isPresent()) {
            Optional<Form> form = createWorkspace.get().getFirstForm();
            TDHttpRequest request = new TDHttpRequest(form.get(), TD.invokeAction);
            request.addHeader("Slug", name);
            request.addHeader("X-Agent-WebID", "");
            TDHttpResponse response = request.execute();
            System.out.println("Status code: " + response.getStatusCode());
        } else {
            System.out.println("No action found");
        }

        /**
         * read the thing description of the workspace
         * retrieve the action to create an artifact
         * if the action exits, create a new artifact with thing description to interact with db
         */

        // read the thing description of the workspace
        ThingDescription tdWorkspace = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT + "workspaces/" + name);

        // retrieve the action to create a new artifact in order to interact with the service itself
        Optional<ActionAffordance> action = tdWorkspace.getActionByName("makeArtifact");

        // if the action is valid, create a new artifact
        if (action.isPresent()) {

            // create a new form because the default form only allows Content-Type: application/json
            Form form = new Form.Builder("http://yggdrasil:8080/workspaces/db/artifacts/")
                    .setMethodName("POST")
                    .setContentType("text/turtle")
                    .addOperationType(TD.invokeAction)
                    .build();

            // create a request
            TDHttpRequest requestArtifact = new TDHttpRequest(form, TD.invokeAction);
            requestArtifact.addHeader("Slug", telemetry);
            requestArtifact.addHeader("X-Agent-WebID", "");

            // create a form to make a get request for the temperature
            Form getTemperatureForm = new Form.Builder("http://db:7600/get/temperature")
                .setMethodName("GET")
                .build();

            // create the action affordance
            ActionAffordance getTemperature = new ActionAffordance.Builder("getTemperature", getTemperatureForm)
                    .addTitle("Get temperature")
                    .build();

            // create a new thing description for the artifact
            ThingDescription tdTemperature = (new ThingDescription.Builder("Interaction with db"))
                .addThingURI("http://db:7600")
                .addAction(getTemperature)
                .build();
            String description = new TDGraphWriter(tdTemperature)
                .write();

            // add td to the body
            StringSchema schema = new StringSchema.Builder().build();
            requestArtifact.setPrimitivePayload(schema, description);

            TDHttpResponse responseArtifact = requestArtifact.execute();

            System.out.println("Status code: " + responseArtifact.getStatusCode());
        } else {
            System.out.println("No action found");
        }
    }
}

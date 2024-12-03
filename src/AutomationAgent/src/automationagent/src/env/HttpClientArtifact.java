import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.ThingDescription.TDFormat;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpResponse;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import ch.unisg.ics.interactions.wot.td.schemas.ObjectSchema;
import ch.unisg.ics.interactions.wot.td.schemas.StringSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;

import static org.eclipse.rdf4j.model.util.Values.iri;


public class HttpClientArtifact extends Artifact{

    private static String SLUG = "AutomationAgent";
    private static String ENTRYPOINT = "http://yggdrasil:8080/";
    private static String WEBID = "http://yggdrasil:8080/workspaces/room1/artifacts/AutomationAgent";
    private static String OMI = "http://yggdrasil:8080/workspaces/root/artifacts/omi";

    void init(){}

    /* Get a list of all workspaces
     * 
     */
    public Optional<Set<Value>> getWorkspaces() {
        // Create a new set with workspaces
        Set<Value> workspaces;

        try {
             // Get and parse the TD from the entrypoint
            ThingDescription td = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, ENTRYPOINT);

             // Get the graph
            final var model = td.getGraph().get();

            // Filter graph by workspaces
            final var filtered = model.filter(null, iri("https://purl.org/hmas/hosts"), null);
            
            // Get all workspace uris
            workspaces = filtered.objects();

            return Optional.of(workspaces);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    /*
     * 
     */
    public void createArtifact(String workspace) {
        
        try {
            
            ThingDescription td = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, workspace);

            Optional<ActionAffordance> action = td.getActionByName("createArtifact");

            if (action.isPresent()) {

                TDHttpRequest request = new TDHttpRequest(action.get().getFirstForm().orElseThrow(), TD.invokeAction);

                request.addHeader("Slug", "AutomationAgent");
                request.addHeader("X-Agent-WebID", WEBID);
                
                final var metadata = Files.readString(Path.of("resources/metadata.ttl"));
    
                StringSchema schema = new StringSchema.Builder().build();
     
                request.setPrimitivePayload(schema, metadata);

                System.out.println(request);
                TDHttpResponse response = request.execute();

                System.out.println("Status code: " + response.getStatusCode());

            }

        } catch (Exception e) {
            System.out.println("ERROR!");
            // TODO: handle exception
        }
        
    }

    /* Join a specific workspace
     * Operation receives a workspace name
     */
    @OPERATION
    public void joinYggdrasil(final String workspaceName, OpFeedbackParam<Boolean> success) {
    
        // Get all workspace uris
        Optional<Set<Value>> workspaces = getWorkspaces();

        if (workspaces.isPresent()) {

            // Loop through each workspace
            workspaces.get().forEach((workspace) -> {
                
                // If a workspace contains the room within the uri ...
                if (workspace.toString().contains(workspaceName.replace("'", ""))) {
                    
                    createArtifact(workspace.stringValue());

                    // Get Formular to join the workspace, and do it!
                    // Join the workspace
                    System.out.println(workspace);
                }
            });

            // Return to the agent that the joining of the workspace was successful
            success.set(true);
        } else {
            success.set(false);
        }
    }

    /*
     * 
     */
    @OPERATION
    public void adoptRole(String roleId, String groupId, OpFeedbackParam<Boolean> success) {
        
        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, OMI);

            Optional<ActionAffordance> action = td.getActionByName("adoptRole");

            if (action.isPresent()) {
                TDHttpRequest request = new TDHttpRequest (
                    action.get().getFirstFormForSubProtocol(TD.invokeAction, "http").orElseThrow(),
                    TD.invokeAction
                );

                request.addHeader("Slug", SLUG);
                request.addHeader("X-Agent-WebID", WEBID);

                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                if (inputSchema.isPresent()) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("agentId", WEBID);
                    payload.put("groupId", groupId);
                    payload.put("roleId", roleId);

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
       // TODO: 


    
        success.set(true);
    }
    /* 
     * 
     */
    public String readEndpoint(String urir) {
        try {
            URI uri = new URI(urir);

            HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();

            HttpClient client = HttpClient.newHttpClient();
            try {
                System.out.println(request.toString());
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    System.out.println("Error: " + response.statusCode());
                }
                String responseBody = response.body();
                return responseBody;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 
     */
    @OPERATION
    public void readEndpoint(String resourceUri, OpFeedbackParam<String> result) {
        try {
            URI uri = new URI(resourceUri);

            HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();

            HttpClient client = HttpClient.newHttpClient();
            try {
                System.out.println(request.toString());
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    System.out.println("Error: " + response.statusCode());
                }
                String responseBody = response.body();
                result.set(responseBody);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /*
     * 
     */
    @OPERATION
    public void getEntrypoint(final String uri, OpFeedbackParam<String> representation, OpFeedbackParam<Boolean> success) {
        final String result = readEndpoint(uri);
        if (result == null) {
            success.set(false);
        } else {
            success.set(true);
            representation.set(result);
        }

    }

    /*
     * 
     */
    @OPERATION
    public void getWorkspace(String workspaceName, String entrypointTDString,OpFeedbackParam<Boolean> found, OpFeedbackParam<String> result) {
        ThingDescription td = TDGraphReader.readFromString(TDFormat.RDF_TURTLE, entrypointTDString);
        final var expectedWorkspaceURI = ENTRYPOINT + "workspaces/" + workspaceName +"/#workspace";
        final var foundWorkspace = findWorkspace(td, workspaceName, expectedWorkspaceURI);
        found.set(foundWorkspace);
        result.set(cleanUri(expectedWorkspaceURI));

    }

    /*
     * 
     */
    public boolean findWorkspace(ThingDescription td, final String workspaceName, final String expectedWorkspaceURI) {
        final var model = td.getGraph().get();
        final var t = model.filter(null, iri("https://purl.org/hmas/hosts"), iri(expectedWorkspaceURI));
        return t.size() > 0;
    }

    /*
     * 
     */
    @OPERATION
    public void findOrganisationInWorkspace(final String workspaceRepresenation, OpFeedbackParam<Boolean> found,
                                            OpFeedbackParam<String> orgArtifactUri){
        final var model = TDGraphReader.readFromString(TDFormat.RDF_TURTLE, workspaceRepresenation).getGraph().get();
        final var artifactsInWorkspace = model.filter(null, iri("https://purl.org/hmas/contains"),null);
        if (artifactsInWorkspace.isEmpty()) {
           System.out.println("No org artifact present");
           found.set(false);
           return;
        }
        final var uncleanedArtifactUri = artifactsInWorkspace.stream().findFirst().get().getObject().toString();
        orgArtifactUri.set(cleanUri(uncleanedArtifactUri));
        found.set(true);
    }

    /*
     * 
     */
    private String cleanUri(final String ucleanUri){
        return ucleanUri.replaceAll("/#[^/]*$", "");
    }

    /*
     * 
     */
    @OPERATION
    public void joinOrganisation(final String orgArtifactRepresentation, OpFeedbackParam<Boolean> success){
        System.out.println(orgArtifactRepresentation);
    }

}

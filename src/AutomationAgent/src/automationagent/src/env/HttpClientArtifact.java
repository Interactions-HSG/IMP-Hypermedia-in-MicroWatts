import static org.eclipse.rdf4j.model.util.Values.iri;

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


public class HttpClientArtifact extends Artifact{

    private static String SLUG = "AutomationAgent";
    private static String ENTRYPOINT = "http://yggdrasil:8080/";
    private static String WEBID = "http://yggdrasil:8080/workspaces/room1/artifacts/AutomationAgent";
    private static String OMI = "http://yggdrasil:8080/workspaces/root/artifacts/omi";
    private static String DB = "http://yggdrasil:8080/workspaces/root/artifacts/datalake";

    void init(){}

    /* The getWorkspaces function retrieves a set of workspaces from a graph representation of a Thing Description (TD) associated with yggdrasil.
     * The return value is an optional data structure containing workspaces URIs if successfully retrieved. 
     * Otherwise, the function returns an empty optional if an exception occurs during the process.
     */
    public Optional<Set<Value>> getWorkspaces() {

        System.out.println("Log: Find all workspaces in yggdrasil");

        // Create a new set with workspaces
        Set<Value> workspaces;

        try {

            System.out.println("Log: Read the ThingDescription of yggdrasil");

             // Get and parse the TD from the entrypoint
            ThingDescription td = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, ENTRYPOINT);

            System.out.println("Log: Process the information from the graph to find all workspaces");

             // Get the graph
            final var model = td.getGraph().get();

            // Filter graph by workspaces
            final var filtered = model.filter(null, iri("https://purl.org/hmas/hosts"), null);
            
            // Get all workspace uris
            workspaces = filtered.objects();

            System.out.println("Log: This workspaces were found: " + workspaces);

            return Optional.of(workspaces);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    /* Create an artifact in the specified workspace by invoking the "createArtifact" action.
     *
     */
    public void createArtifact(String workspace) {
        
        System.out.println("Log: Create a body artifact to interact with the Automation Agent");

        try {

            System.out.println("Log: Read the ThingDescription of a specified workspace");
            
            // Get and parse the TD from a workspace
            ThingDescription td = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, workspace);

            System.out.println("Log: Find action by name");

            // Find action by name 
            Optional<ActionAffordance> action = td.getActionByName("createArtifact");

            // Check if the action is present in the Thing Description
            if (action.isPresent()) {

                System.out.println("Log: Action found");

                // Create a request to execute the action with the first available form
                TDHttpRequest request = new TDHttpRequest(action.get().getFirstForm().orElseThrow(), TD.invokeAction);

                request.addHeader("Slug", "AutomationAgent");
                request.addHeader("X-Agent-WebID", WEBID);
                
                // Read metadata of the Automation Agent
                final var metadata = Files.readString(Path.of("resources/metadata.ttl"));
    
                StringSchema schema = new StringSchema.Builder().build();
     
                System.out.println("Log: Set payload");

                // Add metadata to the request payload
                request.setPrimitivePayload(schema, metadata);

                System.out.println("Log: Execute action");

                TDHttpResponse response = request.execute();

                if (response.getStatusCode() == 201) {
                    System.out.println("Log: Action was successfully executed");
                } else {
                    System.out.println("Error: Action was not executed");
                }

            } else {
                System.out.println("Log: Action not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /* Destroy an artifact in the specified workspace by invoking the "destroyArtifact" action.
     * 
     */
    public void destroyArtifact(String workspaceName) {

        System.out.println("Log: destroy artifact");

        try {

            // Read the Thing Description (TD) from the specified workspace name
            ThingDescription td = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, workspaceName);

            // Retrieve the "destroyArtifact" action from the TD, if it exists
            Optional<ActionAffordance> action = td.getActionByName("destroyArtifact");

            // Check if the action is present in the Thing Description
            if (action.isPresent()) {

                System.out.println("Log: Action found");
                
                // Create a request to execute the action using the first available form
                TDHttpRequest request = new TDHttpRequest(action.get().getFirstForm().orElseThrow(), TD.invokeAction);

                // Add necessary headers to the request
                request.addHeader("Slug", "AutomationAgent");
                request.addHeader("X-Agent-WebID", WEBID);
                
                System.out.println("Log: Execute action");

                // Execute the request and get the response
                TDHttpResponse response = request.execute();

                if (response.getStatusCode() == 201) {
                    System.out.println("Log: Action was successfully executed");
                } else {
                    System.out.println("Error: Action was not executed");
                }
            }

        } catch (Exception e) {
            // Print the stack trace if any exception occurs
            e.printStackTrace();
        }
    }

    
    /* The operation receives a workspace name, searches for the corresponding workspace URI, 
     * and attempts to join it by creating an artifact.
     * The success of the operation is returned as feedback.
     */
    @OPERATION
    public void joinRoom(final String workspaceName, OpFeedbackParam<Boolean> success) {
    
        System.out.println("Log: Join room " + workspaceName);

        // Retrieve all workspace URIs
        Optional<Set<Value>> workspaces = getWorkspaces();

        // Check if workspaces are available
        if (workspaces.isPresent()) {

            System.out.println("Log: Workspaces " + workspaces + "are found");

            // Loop through each workspace URI
            workspaces.get().forEach((workspace) -> {
                
                // Check if the workspace URI contains the specified workspace name
                if (workspace.toString().contains(workspaceName.replace("'", ""))) {
                    
                    // Create an artifact for the specified workspace
                    createArtifact(workspace.stringValue());
                    
                    System.out.println("Log: Body artifact is created");
                }
            });

            // Return to the agent that the joining of the workspace was successful
            success.set(true);
        } else {
            success.set(false);
        }
    }

    /* The operation receives a workspace name, searches for the corresponding workspace URI, 
     * and attempts to leave it by creating an artifact.
     * The success of the operation is returned as feedback.
     */
    @OPERATION
    public void leaveRoom(final String workspaceName, OpFeedbackParam<Boolean> success) {

        System.out.println("Log: Leave room " + workspaceName);

        // Retrieve all workspace URIs
        Optional<Set<Value>> workspaces = getWorkspaces();

        // Check if workspaces are available
        if (workspaces.isPresent()) {

            System.out.println("Log: Workspaces " + workspaces + "are found");
            
            // Loop through each workspace URI
            workspaces.get().forEach((workspace) -> {
                
                if (workspace.toString().contains(workspaceName.replace("'", ""))) {
                    
                    destroyArtifact(workspace.stringValue());

                    System.out.println("Log: Body artifact is destroyed");
                }

            });

            // Return to the agent that the leaving of the workspace was successful
            success.set(true);
        } else {
            success.set(false);
        }

    }

    /* The operation takes a role ID and a group ID, and attempts to adopt the role 
     * by invoking the "adoptRole" action on a Thing Description (TD). 
     * The success of the operation is returned as feedback.
     */
    @OPERATION
    public void adoptRole(String roleId, String groupId, OpFeedbackParam<Boolean> success) {
        
        try {
            // Retrieve the Thing Description (TD) for the specified OMI
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, OMI);

            // Retrieve the "adoptRole" action from the TD
            Optional<ActionAffordance> action = td.getActionByName("adoptRole");

            // Check if the action is present
            if (action.isPresent()) {

                // Create a request for the action using the first available form
                TDHttpRequest request = new TDHttpRequest (
                    action.get().getFirstFormForSubProtocol(TD.invokeAction, "http").orElseThrow(),
                    TD.invokeAction
                );
                
                // Add necessary headers to the request
                request.addHeader("Slug", SLUG);
                request.addHeader("X-Agent-WebID", WEBID);

                // Retrieve the input schema for the action
                Optional<DataSchema> inputSchema = action.get().getInputSchema();

                // If the input schema is present, construct and send the payload
                if (inputSchema.isPresent()) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("agentId", WEBID);
                    payload.put("groupId", groupId);
                    payload.put("roleId", roleId);

                    // Set the payload according to the input schema
                    request.setObjectPayload((ObjectSchema) inputSchema.get(), payload);

                    // Execute the request
                    TDHttpResponse response = request.execute();

                    if (response.getStatusCode() == 201) {
                        System.out.println("Log: Action was successfully executed");
                    } else {
                        System.out.println("Error: Action was not executed");

                        success.set(false);
                    }

                } else {
                    System.out.println("Input schema not found");
                    success.set(false);
                }

            } else {
                System.out.println("Action not found");
                success.set(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success.set(false);
        }
        success.set(true);
    }

    /* Commit to a specific mission
     * The operation takes a mission ID, scheme ID, and goal ID, and attempts to commit 
     * to the mission by invoking the "commitToMission" action on a Thing Description (TD). 
     * The success of the operation is returned as feedback.
     */
    @OPERATION
    public void commitToMission(String missionId, String schemeId, String goalId, OpFeedbackParam<Boolean> success) {
        try {
            // Retrieve the Thing Description (TD) for the specified OMI
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, OMI);

            // Retrieve the "commitToMission" action from the TD
            Optional<ActionAffordance> action = td.getActionByName("commitToMission");

            // Check if the action is present
            if (action.isPresent()) {

                // Create a request for the action using the first available form
                TDHttpRequest request = new TDHttpRequest(
                    action.get().getFirstFormForSubProtocol(TD.invokeAction, "http").orElseThrow(),
                    TD.invokeAction
                );

                // Add necessary headers to the request
                request.addHeader("Slug", SLUG);
                request.addHeader("X-Agent-WebID", WEBID);

                // Retrieve the input schema for the action
                Optional<DataSchema> inputScheme = action.get().getInputSchema();

                // If the input schema is present, construct and send the payload
                if (inputScheme.isPresent()) {
                    Map<String, Object> payload = new HashMap<>();

                    payload.put("agentId", WEBID);
                    payload.put("missionId", missionId);
                    payload.put("schemeId", schemeId);
                    payload.put("goalId", goalId);

                    // Set the payload
                    request.setObjectPayload((ObjectSchema) inputScheme.get(), payload);

                    // Execute the request
                    TDHttpResponse response = request.execute();

                    if (response.getStatusCode() == 200) {
                        System.out.println("Log: Commitment to mission was successful");
                    } else {
                        System.out.println("Log: Commitment to mission was not successful");

                        success.set(false);
                    }
                    
                } else {
                    success.set(false);
                    System.out.println("Input scheme not found.");
                }

            } else {

                success.set(false);
                System.out.println("Action not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     */
    @OPERATION
    public void getSensorData(String type, OpFeedbackParam<String> response, OpFeedbackParam<Boolean> success) {
        try {
            // Retrieve the Thing Description (TD) for the specified OMI
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, DB);

            // Retrieve the "commitToMission" action from the TD
            Optional<ActionAffordance> action = td.getActionByName("get" + type);

            // Check if the action is present
            if (action.isPresent()) {

                // Create a request for the action using the first available form
                TDHttpRequest request = new TDHttpRequest(
                    action.get().getFirstFormForSubProtocol(TD.readProperty, "http").orElseThrow(),
                    TD.readProperty
                );

                // Add necessary headers to the request
                request.addHeader("Slug", SLUG);
                request.addHeader("X-Agent-WebID", WEBID);

                // Retrieve the input schema for the action
                Optional<DataSchema> inputScheme = action.get().getInputSchema();

                // Execute the request
                TDHttpResponse r = request.execute();
                success.set(true);
                response.set(r.getPayload().get());

            } else {

                success.set(false);
                System.out.println("Action not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

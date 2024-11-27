
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

import com.github.jsonldjava.utils.Obj;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.ThingDescription.TDFormat;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import jason.stdlib.foreach;

import static org.eclipse.rdf4j.model.util.Values.iri;


public class HttpClientArtifact extends Artifact{

    private static String ENTRYPOINT = "http://127.0.0.1:8080/";
        
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

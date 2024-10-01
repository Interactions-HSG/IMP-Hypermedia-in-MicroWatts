
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.ThingDescription.TDFormat;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;

import static org.eclipse.rdf4j.model.util.Values.iri;



public class HttpClientArtifact extends Artifact{

    private static String ENTRYPOINT = "http://localhost:8080/";

    void init(){}

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

    @OPERATION
    public void getWorkspace(String workspaceName, String entrypointTDString, OpFeedbackParam<String> result) {
        ThingDescription td = TDGraphReader.readFromString(TDFormat.RDF_TURTLE, entrypointTDString);
        final var expectedWorkspaceURI = ENTRYPOINT + "workspaces/" + workspaceName +"/#workspace";
        final var foundWorkspace = findWorkspace(td, workspaceName, expectedWorkspaceURI);
        System.out.println(foundWorkspace);
        final var workspaceUri = ENTRYPOINT + "workspaces/" + workspaceName;
        result.set(workspaceUri);

    }


    public boolean findWorkspace(ThingDescription td, final String workspaceName, final String expectedWorkspaceURI) {
        final var model = td.getGraph().get();
        final var t = model.filter(null, iri("https://purl.org/hmas/hosts"), iri(expectedWorkspaceURI));
        return t.size() > 0;
    }

    @OPERATION
    public void joinOrganisationInWorkspace(final String workspaceRepresenation){
        final var model = TDGraphReader.readFromString(TDFormat.RDF_TURTLE, workspaceRepresenation).getGraph().get();
        final var artifactsInWorkspace = model.filter(null, iri("https://purl.org/hmas/contains"),null);
        if (artifactsInWorkspace.isEmpty()) {
           System.out.println("No org artifact present");
        }
    }

}

package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.ThingDescription.TDFormat;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;

public class Launcher {
	
	public static String fileName = "org.xml";
	public static String ENTRYPOINT = "http://localhost:8080";
	
	static {
		UdpConfig.register();
		TcpConfig.register();
	}

	public static void main(String[] args) {
		try {
			// create server
			System.out.printf("Intializing OrgServer from %s\n", fileName);
			MoiseOrgServer server = new MoiseOrgServer(3685);
			server.start();

			// register OrgManager Artifact in Yggdrasil root workspace
			HttpClient client = HttpClient.newHttpClient();
			final var entryPointRepresentation = readEndpoint(ENTRYPOINT);
			final var rootWorkspaceUri = getWorkspace("root", entryPointRepresentation);
			final var rootWorkspaceRepresentation = readEndpoint(rootWorkspaceUri);
			final var makeArtifactInstructions = findMakeArtifactInstructions(rootWorkspaceRepresentation);



		} catch (SocketException e) {
			System.err.println("Failed to initialize server: " + e.getMessage());
		}
	}

    static public String readEndpoint(String resourceUri) {
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
              return response.body();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
		return null;
    }

    static public String getWorkspace(String workspaceName, String entrypointTDString) {
        ThingDescription td = TDGraphReader.readFromString(TDFormat.RDF_TURTLE, entrypointTDString);
        final var expectedWorkspaceURI = ENTRYPOINT + "workspaces/" + workspaceName +"/#workspace";
        final var foundWorkspace = findWorkspace(td, workspaceName, expectedWorkspaceURI);
        System.out.println(foundWorkspace);
      return ENTRYPOINT + "workspaces/" + workspaceName;

    }


    static public boolean findWorkspace(ThingDescription td, final String workspaceName, final String expectedWorkspaceURI) {
        final var model = td.getGraph().get();
        final var t = model.filter(null, iri("https://purl.org/hmas/hosts"), iri(expectedWorkspaceURI));
        return !t.isEmpty();
    }

	static ActionAffordance findMakeArtifactInstructions(final String workspaceRepresentation){

		return null;
	}
}

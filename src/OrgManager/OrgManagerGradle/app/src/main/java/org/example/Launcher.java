package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.ThingDescription.TDFormat;
import ch.unisg.ics.interactions.wot.td.clients.TDHttpRequest;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.schemas.StringSchema;
import ch.unisg.ics.interactions.wot.td.vocabularies.TD;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import org.eclipse.rdf4j.model.Model;

public class Launcher {

  public static String fileName = "org.xml";
  public static String ENTRYPOINT = System.getenv("ENTRYPOINT");
  public static String ROOT_WORKSPACE = "root";
  public static String METADATA;
  static {
    try {
      METADATA = Files.readString(new File("metadata.ttl").toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static void main(String[] args) {
    try {
      // create server
      System.out.printf("Intializing OrgServer from %s\n", fileName);
      AGRServer server = new AGRServer(5686);
      //MoiseOrgServer server = new MoiseOrgServer(5686);
      server.start();
      TestClient.createDemoRole();

      // register OrgManager Artifact in Yggdrasil root workspace
      final var platformTD = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, ENTRYPOINT);
      final var rootWorkspaceUri = getWorkspace(ROOT_WORKSPACE, platformTD);
      final var rootWorkspaceTD = TDGraphReader.readFromURL(TDFormat.RDF_TURTLE, rootWorkspaceUri);
      final var createArtifact = createArtifact(rootWorkspaceTD);


    } catch (SocketException e) {
      System.err.println("Failed to initialize server: " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static public String getWorkspace(String workspaceName, ThingDescription platformTD) {
    final var foundWorkspace = findWorkspace(
        platformTD.getGraph().orElseThrow(), workspaceName);
    if (!foundWorkspace) {
      System.out.println("Workspace not found: " + workspaceName);
      return null;
    }
    return ENTRYPOINT + "workspaces/" + workspaceName;

  }


  static public boolean findWorkspace(Model td, final String workspaceName) {
    final var t = td.filter(null, iri("https://purl.org/hmas/hosts"), null);
    if (t.isEmpty()) {
      System.out.println("No workspaces found");
      return false;
    }
    t.objects().forEach(o -> {
      System.out.println("Found workspace: " + o.stringValue());
    });
    final var workspaceTripleCount =
        t.stream().filter(s -> s.getObject().stringValue().contains(workspaceName)).count();

    if (workspaceTripleCount == 0) {
      System.out.println("No workspace found: " + workspaceName);
      return false;
    }
    if (workspaceTripleCount > 1) {
      System.out.println("Multiple workspaces found for: " + workspaceName);
      return false;
    }
    return true;
  }

  static boolean createArtifact(
      final ThingDescription workspaceRepresentation) throws IOException {
    final var createArtifactAction = workspaceRepresentation.getActionByName("createArtifact");
    if (createArtifactAction.isEmpty()) {
      System.out.println("No createArtifact action found");
      return false;
    }
    final var createArtifactAffordance = createArtifactAction.get();
    final var createArtifactForm = createArtifactAffordance.getFirstForm();
    if (createArtifactForm.isEmpty()) {
      System.out.println("No form found for createArtifact action");
      return false;
    }

    TDHttpRequest request = new TDHttpRequest(createArtifactForm.get(), TD.invokeAction);
    request.addHeader("X-Agent-WebID","http://organization_manager:8083/");
    request.addHeader("Slug","OrgManager");
    request.setPrimitivePayload(new StringSchema.Builder().build(), METADATA);
    final var response = request.execute();

    System.out.println("Create Artifact response code: " + response.getStatusCode());
    return true;

  }
}

package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import java.io.IOException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

public class SensingAgent extends CoapServer {

  private final String ENTRYPOINT;
  private final String metadata;
  private String baseUriYggdrasil;

  private boolean running = false;

  public SensingAgent(String entrypoint, String metadata) {
    super(5684);
    this.ENTRYPOINT = entrypoint;
    this.metadata = metadata;
  }

  public void run() throws ConnectorException, IOException {
    this.start();
    this.setup();


    //running = true;
    // final var foundDB = findDB();
    final var joinedRootWorkspace = joinRootWorkspace();
    while (running) {
      System.out.println("Sensing...");
      sendSensingData();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    this.stop();
    this.destroy();
  }

  /**
   * Helper method that sets up the base URI of the Yggdrasil platform.
   * Needed because base URI is an HTTP URI and the TD representations returned
   * by the platform all build on this uri.
   */
  private void setup() throws ConnectorException, IOException {
    final var coapClient = new CoapClient(ENTRYPOINT);
    final String platformRepresentation = coapClient.get().getResponseText();
    final var td = TDGraphReader.
        readFromString(ThingDescription.TDFormat.RDF_TURTLE, platformRepresentation);
    baseUriYggdrasil = cleanUri(td.getThingURI().orElseThrow());
    coapClient.shutdown();
  }

  public void stopRunning() {
    running = false;
  }

  private String sendCoapMessage(final String targetUri) throws ConnectorException, IOException {
    final var coapClient = new CoapClient(targetUri);
    final var response = coapClient.get();
    final var responseText = response.getResponseText();
    coapClient.shutdown();
    return responseText;
  }

  private boolean findDB() throws ConnectorException, IOException {
    final String platformRepresentation = sendCoapMessage(ENTRYPOINT);
    final String rootWorkspaceUri = getWorkspaceUri("root", platformRepresentation);
    if (rootWorkspaceUri == null) {
      System.out.println("DB not found");
      return false;
    }

    final String rootWorkspaceRepresentation = sendCoapMessage(rootWorkspaceUri);
    final String DBArtifactUri = getArtifactUri("DB", rootWorkspaceRepresentation);
    if (DBArtifactUri == null) {
      System.out.println("DB not found");
      return false;
    }
    final String DBRepresentation = sendCoapMessage(DBArtifactUri);
    System.out.println("DB found: " + DBRepresentation);
    running = false;
    return true;
  }

  private void sendSensingData() throws ConnectorException, IOException {
    System.out.println("Sending sensing data...");
    final var coapClient = new CoapClient(ENTRYPOINT);
    coapClient.post("sensing data", 0);
    coapClient.shutdown();

  }

  private boolean joinRootWorkspace() throws ConnectorException, IOException {
    final String platformRepresentation = sendCoapMessage(ENTRYPOINT);
    final String rootWorkspaceUri = getWorkspaceUri("root", platformRepresentation);

    if (rootWorkspaceUri == null) {
      System.out.println("Root workspace not found");
      return false;
    }

    final String rootWorkspaceRepresentation = sendCoapMessage(rootWorkspaceUri);
    final String joinFormUri = getJoinFormUri(rootWorkspaceRepresentation);
    if (joinFormUri == null) {
      System.out.println("Join form not found");
      return false;
    }


    final var coapClient = new CoapClient(joinFormUri);
    Request request = new Request(CoAP.Code.POST, CoAP.Type.NON);
    OptionSet optionSet = new OptionSet();
    optionSet.addOption(new Option(
        500, String.valueOf(this.getEndpoints().getFirst().getUri())));
    optionSet.addOption(new Option(600, "SensingAgent"));
    request.setOptions(optionSet);
    request.setPayload(this.metadata);
    final var response = coapClient.advanced(request); // or async version
    System.out.println("Join response: " + response.getResponseText());
    coapClient.shutdown();
    return true;
  }

  private String getJoinFormUri(String rootWorkspaceRepresentation) {
    ThingDescription td = TDGraphReader
        .readFromString(ThingDescription.TDFormat.RDF_TURTLE, rootWorkspaceRepresentation);
    final var joinWorkspaceAffordance = td.getActionByName("joinWorkspace");
    if (joinWorkspaceAffordance.isPresent()) {
      final var joinWorkspaceAction = joinWorkspaceAffordance.get();
      final var joinWorkspaceForm = joinWorkspaceAction.getFirstForm();
      System.out.println("Join form found: " + joinWorkspaceForm.get().getTarget());
      return ENTRYPOINT + "workspaces/root/join";
    }
    return null;
  }

  public String getWorkspaceUri(String workspaceName, String entrypointTDString) {
    ThingDescription td =
        TDGraphReader.readFromString(ThingDescription.TDFormat.RDF_TURTLE, entrypointTDString);
    final var expectedWorkspaceURI = baseUriYggdrasil + "/workspaces/" + workspaceName +
        "/#workspace";
    final var foundWorkspace = findWorkspaceUri(td, expectedWorkspaceURI);
    if (foundWorkspace) {
      return ENTRYPOINT + "workspaces/" + workspaceName;
    } else {
      return null;
    }
  }

  public String getArtifactUri(String artifactName, String workspaceRepresenation) {
    ThingDescription td =
        TDGraphReader.readFromString(ThingDescription.TDFormat.RDF_TURTLE, workspaceRepresenation);
    final var workspaceUri = cleanUri(td.getThingURI().get());
    final var expectedArtifactURI = workspaceUri + "artifacts/" + artifactName + "/#artifact";
    final var foundArtifact = findArtifactUri(td, expectedArtifactURI);
    if (foundArtifact) {
      return cleanUri(expectedArtifactURI);
    } else {
      return null;
    }
  }

  public boolean findArtifactUri(ThingDescription td, final String expectedArtifactURI) {
    final var model = td.getGraph().get();
    final var t =
        model.filter(null, iri("https://purl.org/hmas/contains"), iri(expectedArtifactURI));
    return t.size() > 0;
  }


  public boolean findWorkspaceUri(ThingDescription td, final String expectedWorkspaceURI) {
    final var model = td.getGraph().get();
    final var t = model.filter(null, iri("https://purl.org/hmas/hosts"), iri(expectedWorkspaceURI));
    return t.size() > 0;
  }

  private String cleanUri(final String ucleanUri) {
    return ucleanUri.replaceAll("/#[^/]*$", "");
  }

  public String getRepresentation() {
    return metadata;
  }
}

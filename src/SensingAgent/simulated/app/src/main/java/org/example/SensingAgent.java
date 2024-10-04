package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.exception.ConnectorException;

public class SensingAgent extends CoapServer {

  private final String ENTRYPOINT;
  private final String metadata;
  private final String location;
  private String locationUri;
  private String baseUriYggdrasil;
  private String dbPostSensorDataUri;

  private boolean running = false;
  private ScheduledExecutorService scheduler;

  public SensingAgent(String entrypoint, String metadata, String room) {
    super(5684);
    this.ENTRYPOINT = entrypoint;
    this.metadata = metadata;
    this.location = room;
    this.locationUri = null;
  }

  @Override
  protected Resource createRoot() {
    return new SensingAgentRootResource();
  }

  public void run() throws ConnectorException, IOException {
    this.start();
    this.setup();
    running = true;
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::sendSensingData, 0, 10, TimeUnit.SECONDS);
  }

  /**
   * Helper method that sets up the base URI of the Yggdrasil platform.
   * Needed because base URI is an HTTP URI and the TD representations returned
   * by the platform all build on this uri. Tries to join the workspace currently
   * recognized to be where the agent should be. Also tries to find the DB artifact
   * and link its representation.
   */
  private void setup() throws ConnectorException, IOException {
    System.out.println("Setting up...");
    String platformRepresentation = sendCoapMessage(ENTRYPOINT);
    while(platformRepresentation == null) {
      System.out.println("Failed to get platform representation");
      platformRepresentation = sendCoapMessage(ENTRYPOINT);
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        System.err.println("Failed to sleep");
      }
    }
    final var td = TDGraphReader.
        readFromString(ThingDescription.TDFormat.RDF_TURTLE, platformRepresentation);
    baseUriYggdrasil = cleanUri(td.getThingURI().orElseThrow());

    var joined = joinMyWorkspace();
    while (!joined) {
      System.out.println("Failed to join workspace");
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        System.err.println("Failed to sleep");
      }
      joined = joinMyWorkspace();
    }
    System.out.println("Joined workspace");

    var dbFound = findDB();
    while (!dbFound) {
      System.out.println("Failed to find DB");
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        System.err.println("Failed to sleep");
      }
      dbFound = findDB();
    }
    System.out.println("DB found");
  }

  public void stopRunning() {
    running = false;
    if (scheduler != null) {
      scheduler.shutdown();
    }
    this.stop();
    this.destroy();
  }

  private String sendCoapMessage(final String targetUri) throws ConnectorException, IOException {
    System.out.println("Sending CoAP message to: " + targetUri);
    final var coapClient = new CoapClient(targetUri);
    final var response = coapClient.get();
    if (response == null) {
      System.out.println("Response is null");
      return null;
    }
    final var responseText = response.getResponseText();
    coapClient.shutdown();
    return responseText;
  }

  private boolean findDB() throws ConnectorException, IOException {
    final String platformRepresentation = sendCoapMessage(ENTRYPOINT);
    final String rootWorkspaceUri = getWorkspaceUri("root", platformRepresentation);
    if (rootWorkspaceUri == null) {
      System.out.println("root workspace not found");
      return false;
    }

    final String rootWorkspaceRepresentation = sendCoapMessage(rootWorkspaceUri);
    final String DBArtifactUri = getArtifactUri("datalake", rootWorkspaceRepresentation);
    if (DBArtifactUri == null) {
      System.out.println("datalake not found");
      return false;
    }
    final String DBRepresentation = sendCoapMessage(DBArtifactUri);
    if (DBRepresentation == null) {
      System.out.println("DB representation not found");
      return false;
    }
    final var dbTD = TDGraphReader
        .readFromString(ThingDescription.TDFormat.RDF_TURTLE, DBRepresentation);

    final var dbEndpoint = dbTD.getActionByName("postSensorData");

    if (dbEndpoint.isEmpty()) {
      System.out.println("DB endpoint not found");
      return false;
    }

    if (dbEndpoint.get().getFirstForm().isEmpty()) {
      System.out.println("DB endpoint form not found");
      return false;
    }
    dbPostSensorDataUri = dbEndpoint.get().getFirstForm().get().getTarget();
    System.out.println("DB endpoint: " + dbEndpoint.get().getFirstForm().get().getTarget());
    return true;
  }

  private void sendSensingData() {
    if(!running) {
      return;
    }
    try {
    System.out.println("Sending sensing data...");
    final var coapClient = new CoapClient(dbPostSensorDataUri);
    Request request = new Request(CoAP.Code.POST, CoAP.Type.NON);
    final var randomTemperature = new Random().nextInt(30);
    final var randomHumidity = new Random().nextInt(100);
    request.setPayload("\"temperature\":\" "
        + randomTemperature
        + "\",\"humidity\":\""
        + randomHumidity + "\"");
    coapClient.advanced(request); // or async version
    coapClient.shutdown();
    } catch (Exception e) {
      System.err.println("Failed to send sensing data: " + e.getMessage());
    }
  }

  private boolean joinMyWorkspace() throws ConnectorException, IOException {
    final String platformRepresentation = sendCoapMessage(ENTRYPOINT);
    final String myWorkspaceUri = getWorkspaceUri(location, platformRepresentation);

    if (myWorkspaceUri == null) {
      System.out.println("My workspace not found");
      return false;
    }

    final String myWorkspaceRepresentation = sendCoapMessage(myWorkspaceUri);
    final String joinFormUri = getJoinFormUri(location, myWorkspaceRepresentation);
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
    if (response == null) {
      System.out.println("Join response: null - failed to join");
      return false;
    }
    System.out.println("Join response: " + response.getResponseText());
    this.locationUri = myWorkspaceUri;
    coapClient.shutdown();
    return true;
  }

  private String getJoinFormUri(String workspaceName, String WorkspaceRepresentation) {
    if (WorkspaceRepresentation == null) {
      return null;
    }
    ThingDescription td = TDGraphReader
        .readFromString(ThingDescription.TDFormat.RDF_TURTLE, WorkspaceRepresentation);
    final var joinWorkspaceAffordance = td.getActionByName("joinWorkspace");
    if (joinWorkspaceAffordance.isPresent()) {
      final var joinWorkspaceAction = joinWorkspaceAffordance.get();
      final var joinWorkspaceForm = joinWorkspaceAction.getFirstForm();
        if (joinWorkspaceForm.isEmpty()) {
            System.out.println("Join form not found");
            return null;
        }
      System.out.println("Join form found: " + joinWorkspaceForm.get().getTarget());
      return ENTRYPOINT + "workspaces/" + workspaceName + "/join";
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
    if (td == null || td.getThingURI().isEmpty()) {
      return null;
    }
    final var workspaceUri = cleanUri(td.getThingURI().get());
    final var expectedArtifactURI = workspaceUri + "/artifacts/" + artifactName + "/#artifact";
    final var foundArtifact = findArtifactUri(td, expectedArtifactURI);
    if (foundArtifact) {
      return cleanUri(expectedArtifactURI);
    } else {
      return null;
    }
  }

  public boolean findArtifactUri(ThingDescription td, final String expectedArtifactURI) {
    if (td == null || td.getGraph().isEmpty()) {
      return false;
    }
    final var model = td.getGraph().get();
    final var t =
        model.filter(null, iri("https://purl.org/hmas/contains"), iri(expectedArtifactURI));
    return !t.isEmpty();
  }


  public boolean findWorkspaceUri(ThingDescription td, final String expectedWorkspaceURI) {
    if (td == null || td.getGraph().isEmpty()) {
      return false;
    }
    final var model = td.getGraph().get();
    final var t = model.filter(null, iri("https://purl.org/hmas/hosts"), iri(expectedWorkspaceURI));
    return !t.isEmpty();
  }

  private String cleanUri(final String ucleanUri) {
    return ucleanUri.replaceAll("/#[^/]*$", "");
  }

  public String getRepresentation() {
    return metadata;
  }
}

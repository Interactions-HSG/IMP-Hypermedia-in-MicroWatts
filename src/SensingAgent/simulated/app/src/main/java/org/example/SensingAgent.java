package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.affordances.Form;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import ch.unisg.ics.interactions.wot.td.io.TDGraphWriter;
import ch.unisg.ics.interactions.wot.td.schemas.DataSchema;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.elements.exception.ConnectorException;

public class SensingAgent extends CoapServer {

  private final String ENTRYPOINT;
  private String metadata;

  private boolean running = false;
  private CoapClient coapClient;

  public SensingAgent(String entrypoint) throws IOException {
    super(5685);
    this.ENTRYPOINT = entrypoint;
  }

  public void run() throws ConnectorException, IOException {
    this.start();
    this.metadata = getRepresentation();
    //running = true;
    final var foundDB = findDB();
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

  public void stopRunning() {
    running = false;
  }

  private boolean findDB() throws ConnectorException, IOException {
    coapClient = new CoapClient(ENTRYPOINT);
    final String platformRepresentation = coapClient.get().getResponseText();
    System.out.println("Platform found: " + platformRepresentation);
    final String rootWorkspaceUri = getWorkspaceUri("root", platformRepresentation);
    if (rootWorkspaceUri == null) {
      System.out.println("DB not found");
      return false;
    }

    coapClient.setURI(rootWorkspaceUri);
    final String rootWorkspaceRepresentation = coapClient.get().getResponseText();
    final String DBArtifactUri = getArtifactUri("DB", rootWorkspaceRepresentation);
    if (DBArtifactUri == null) {
      System.out.println("DB not found");
      return false;
    }
    coapClient.setURI(DBArtifactUri);
    final String DBRepresentation = coapClient.get().getResponseText();
    System.out.println("DB found: " + DBRepresentation);
    running = false;
    return true;
  }

  private void sendSensingData() throws ConnectorException, IOException {
    System.out.println("Sending sensing data...");
    coapClient.post("sensing data", 0);

  }

  public String getWorkspaceUri(String workspaceName, String entrypointTDString) {
    ThingDescription td =
        TDGraphReader.readFromString(ThingDescription.TDFormat.RDF_TURTLE, entrypointTDString);
    final var expectedWorkspaceURI = ENTRYPOINT + "workspaces/" + workspaceName + "/#workspace";
    final var foundWorkspace = findWorkspaceUri(td, expectedWorkspaceURI);
    if (foundWorkspace) {
      return cleanUri(expectedWorkspaceURI);
    } else {
      return null;
    }
  }

  public String getArtifactUri(String artifactName, String workspaceRepresenation) {
    ThingDescription td =
        TDGraphReader.readFromString(ThingDescription.TDFormat.RDF_TURTLE, workspaceRepresenation);
    final var expectedArtifactURI = ENTRYPOINT + "artifacts/" + artifactName + "/#artifact";
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
    final var tdBuilder = new ThingDescription.Builder("SensingAgent");
    tdBuilder.addThingURI(String.valueOf(this.getEndpoints().getFirst().getUri()));
    tdBuilder.addSemanticType("https://purl.org/hmas/SensingAgent");
    final var td = tdBuilder.addAction(
        new ActionAffordance.Builder("getSensorData",
            new Form.Builder("http://database.com/sensordata")
                .setContentType("application/json")
                .setMethodName("GET")
                .build()
        )
            .addInputSchema(new DataSchema.Builder().build())
            .build())
        .build();


    final var metadata = new TDGraphWriter(td)
        .setNamespace("td", "https://www.w3.org/2019/wot/td#")
        .setNamespace("htv", "http://www.w3.org/2011/http#")
        .setNamespace("hctl", "https://www.w3.org/2019/wot/hypermedia#")
        .setNamespace("wotsec", "https://www.w3.org/2019/wot/security#")
        .setNamespace("dct", "http://purl.org/dc/terms/")
        .setNamespace("js", "https://www.w3.org/2019/wot/json-schema#")
        .setNamespace("hmas", "https://purl.org/hmas/")
        .setNamespace("ex", "http://example.org/")
        .setNamespace("jacamo", "http://jacamo.sourceforge.net/")
        .write();
    System.out.println(metadata);
    return metadata;
  }
}

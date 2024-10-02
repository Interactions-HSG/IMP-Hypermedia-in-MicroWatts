package org.example;

import static org.eclipse.rdf4j.model.util.Values.iri;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import java.io.IOException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.elements.exception.ConnectorException;

public class SensingAgent {

  private final String ENTRYPOINT;

  private boolean running = false;
  private CoapClient coapClient;

  public SensingAgent(String entrypoint) {
    this.ENTRYPOINT = entrypoint;
  }

  public void run() throws ConnectorException, IOException {
    running = true;
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
  }

  public void stop() {
    running = false;
  }

  private boolean findDB() throws ConnectorException, IOException {
    coapClient = new CoapClient(ENTRYPOINT);
    final String platformRepresentation = coapClient.get().getResponseText();
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
}

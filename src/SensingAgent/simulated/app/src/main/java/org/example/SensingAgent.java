package org.example;

import java.io.IOException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.elements.exception.ConnectorException;

public class SensingAgent {

  private boolean running = false;
  private CoapClient coapClient;

  public void run() throws ConnectorException, IOException {
    running = true;
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

    private void sendSensingData() throws ConnectorException, IOException {
        System.out.println("Sending sensing data...");
        coapClient.post("sensing data", 0);

  }
}

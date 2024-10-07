package ch.unisg.db.controllers;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

public class TelemetryController extends CoapResource {

    List<String> telemetryData;

    public TelemetryController(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("data");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.respond(telemetryData.toString());
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        final var payload = exchange.getRequestText();
        telemetryData.add(payload);
        System.out.println("Received payload: " + payload);
        exchange.respond("Received payload.");
    }

}

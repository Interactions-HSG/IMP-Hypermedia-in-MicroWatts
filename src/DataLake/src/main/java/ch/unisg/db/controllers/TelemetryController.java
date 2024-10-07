package ch.unisg.db.controllers;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TelemetryController extends CoapResource {

    HashMap<String, Object> telemetries = new HashMap<>();

    public TelemetryController(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("data");
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        exchange.respond(telemetries.toString());
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        final var payload = exchange.getRequestText();

        Date test = new Timestamp(System.currentTimeMillis());

        telemetries.put(test.toString(), payload);

        System.out.println("Received payload: " + payload);
        exchange.respond("Received payload.");
    }

}

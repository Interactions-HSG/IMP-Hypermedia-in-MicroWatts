package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TelemetryController extends CoapResource {


    private final Database db = Database.getInstance();

    public TelemetryController(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("data");
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        exchange.respond(db.getAllData().toString());
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        final var payload = exchange.getRequestText();

        String[] parts = payload.split(",");
        String key = parts[0];
        db.addData(key, payload);

        System.out.println("Received payload: " + payload);
        exchange.respond("Received payload.");
    }

}

package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TelemetryResource extends CoapResource {

    private final Database database = Database.getInstance();

    public TelemetryResource(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("data");
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        exchange.accept();

        final var telemetries = database.getAll();

        exchange.respond(telemetries.toString());
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        exchange.accept();
        final var payload = exchange.getRequestText();

        String key = payload.split(",")[0];

        database.put(key, payload);

        System.out.println("Received key: " + key + " and payload: " + payload);
        exchange.respond("Received payload.");
    }

}

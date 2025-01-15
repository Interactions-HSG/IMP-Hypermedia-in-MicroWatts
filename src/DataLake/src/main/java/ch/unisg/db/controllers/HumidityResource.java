package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class HumidityResource extends CoapResource {
    private final Database database = Database.getInstance();

    public HumidityResource(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("humidity");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.accept();
        final String humidity = database.get("humidity");
        exchange.respond(humidity);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        final var payload = exchange.getRequestText();

        String key = payload.split(",")[0];

        database.put(key, payload);

        System.out.println("Received payload: " + payload);
        exchange.respond("Received payload.");
    }

}

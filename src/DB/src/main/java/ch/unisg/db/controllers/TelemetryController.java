package ch.unisg.db.controllers;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TelemetryController extends CoapResource {

    public TelemetryController(String uri) {
        super(uri);
        setObservable(true);
        getAttributes().setTitle("data");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.respond("Hello World!");
    }

}

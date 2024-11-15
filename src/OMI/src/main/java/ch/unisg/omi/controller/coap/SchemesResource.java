package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.core.port.in.SchemeUseCase;
import ch.unisg.omi.core.service.SchemeService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

public class SchemesResource extends CoapResource {

    private final SchemeUseCase schemeUseCase;
    private final CoapServerConfig server;

    public SchemesResource(String name) {
        super(name);
        this.schemeUseCase = new SchemeService();
        this.server = CoapServerConfig.getInstance();
    }

    /*
        Get a list of all created schemes
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        // TODO: Implement function to retrieve schemes
    }

    /*
        Start a new scheme and create a new resource endpoint
     */
    @Override
    public void handlePOST(CoapExchange exchange) {

        // retrieve the request
        Request request = exchange.advanced().getRequest();

        // start a scheme
        schemeUseCase.startScheme(request.getPayloadString());

        // create a new resource endpoint
        Resource scheme = server.getRoot().getChild("schemes");
        scheme.add(new SchemeResource(request.getPayloadString()));

        // send a response with the resource endpoint
        Response response = new Response(CoAP.ResponseCode.CREATED);
        exchange.respond(response);
    }
}

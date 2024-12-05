package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import moise.os.ss.Role;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ObservableResource;
import java.util.ArrayList;
import java.util.Collection;

public class RolesResource extends CoapResource implements ObservableResource {

    private final CoapServerConfig server;

    private ArrayList<Role> roles = new ArrayList<>();

    public void addRoles(Collection<Role> roleList) {
        roles.addAll(roleList);
    }

    public void notifyResource() {
        this.notifyObserverRelations(null);
    }

    public RolesResource(String name) {
        super(name);
        this.server = CoapServerConfig.getInstance();
    }

    /*
     *
     */
    @Override
    public void handleGET(CoapExchange exchange) {

        Request request = exchange.advanced().getRequest();

        exchange.respond(CoAP.ResponseCode.CONTENT, this.roles.toString());
    }
}

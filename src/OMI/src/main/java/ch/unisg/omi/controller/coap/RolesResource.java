package ch.unisg.omi.controller.coap;

import ch.unisg.omi.config.CoapServerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Role.class, new RoleSerializer());
        Gson gson = gsonBuilder.create();
        final var response = gson.toJson(this.roles);

        exchange.respond(CoAP.ResponseCode.CONTENT,response);
    }

    private class RoleSerializer implements JsonSerializer<Role> {
        @Override
        public JsonElement serialize(Role role, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            // Add properties of the Role class to the JSON object
            jsonObject.addProperty("name", role.getId());
            // Add other properties as needed
            return jsonObject;
        }
    }
}

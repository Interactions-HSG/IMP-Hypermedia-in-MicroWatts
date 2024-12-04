package ch.unisg.omi.controller.coap;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import ch.unisg.omi.core.service.GroupService;
import com.google.gson.Gson;
import moise.os.fs.Goal;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ObservableResource;

import java.util.ArrayList;

public class GroupResource extends CoapResource implements ObservableResource {


    private ArrayList<Goal> goals = new ArrayList<>();

    private final GroupUseCase groupUseCase;

    public GroupResource(String name, GroupUseCase groupUseCase) {
        super(name);
        this.groupUseCase = groupUseCase;
    }

    /*
     *
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        // TODO: Provide list of available goals
        Request request = exchange.advanced().getRequest();

        Response response = new Response(CoAP.ResponseCode.CONTENT);

        exchange.respond(response);
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void notifyResource() {
        this.notifyObserverRelations(null);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        /*
         * TODO: see metada
         */

        Request request = exchange.advanced().getRequest();

        Gson gson = new Gson();

        Response response = exchange.advanced().getResponse();

        exchange.respond(response);

    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        // TODO: see metadata
        Request request = exchange.advanced().getRequest();
        Gson gson = new Gson();
        Response response = exchange.advanced().getResponse();
        exchange.respond(response);
    }

}

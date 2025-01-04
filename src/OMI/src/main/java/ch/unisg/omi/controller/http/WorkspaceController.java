package ch.unisg.omi.controller.http;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.config.YggdrasilConfig;
import ch.unisg.omi.controller.coap.GroupResource;
import ch.unisg.omi.controller.coap.RolesResource;
import ch.unisg.omi.core.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final Environment env;
    private final YggdrasilConfig yggdrasilConfig;
    private final GroupUseCase groupUseCase;
    private final MissionUseCase missionUseCase;
    private final BroadcastUseCase broadcastUseCase;
    private final RoleUseCase roleUseCase;

    CoapServerConfig server = CoapServerConfig.getInstance();

    @Autowired
    public WorkspaceController(Environment env, GroupUseCase groupUseCase, MissionUseCase missionUseCase, BroadcastUseCase broadcastUseCase, RoleUseCase roleUseCase) {
        this.env = env;
        this.yggdrasilConfig = YggdrasilConfig.getInstance(env);
        this.groupUseCase = groupUseCase;
        this.missionUseCase = missionUseCase;
        this.broadcastUseCase = broadcastUseCase;
        this.roleUseCase = roleUseCase;
    }

    @PostMapping(path = "/workspaces")
    public ResponseEntity<String> newWorkspace(
            @RequestHeader("Location") String location,
            @RequestBody String requestBody
    ) {

        // Log message
        System.out.println("Log: New workspace " + location + " is created.");

        System.out.println(location + "/artifacts/");
        // Subscribe to the workspace to listen for new artifacts.
        yggdrasilConfig.subscribe(location + "/artifacts/", YggdrasilConfig.BASE + "workspaces" +
            "/artifacts");

        // Get the workspace name of the uri
        String workspaceName = location.split("/")[location.split("/").length - 1];

        // Create a new group resource
        server.add(new GroupResource(workspaceName, groupUseCase, roleUseCase, missionUseCase));
        server.getRoot().getChild(workspaceName).add(new RolesResource("roles"));

        // Add a new organization group for the workspace
        groupUseCase.addGroup(workspaceName);

        // Start broadcasting to agents within the group
        broadcastUseCase.broadcast(workspaceName);

        return ResponseEntity.ok().body("OK");

    }
}

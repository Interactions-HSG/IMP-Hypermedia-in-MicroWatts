package ch.unisg.omi.controller.http;

import ch.unisg.omi.config.YggdrasilConfig;
import ch.unisg.omi.core.port.in.BroadcastUseCase;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.SchemeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final YggdrasilConfig yggdrasilConfig = YggdrasilConfig.getInstance();
    private final GroupUseCase groupUseCase;
    private final SchemeUseCase schemeUseCase;
    private final BroadcastUseCase broadcastUseCase;

    @PostMapping(path = "/workspaces")
    public ResponseEntity<String> newWorkspace(
            @RequestHeader("Location") String location,
            @RequestBody String requestBody
    ) {

        System.out.println("New workspace is created.");

        yggdrasilConfig.subscribe(location, "http://omi:7500/workspaces/artifacts");

        String workspaceName = location.split("/")[location.split("/").length - 1];

        groupUseCase.addGroup(workspaceName + "-group");

        schemeUseCase.startScheme(workspaceName + "-scheme");

        broadcastUseCase.broadcast(workspaceName + "-group");

        return ResponseEntity.ok().body("OK");

    }
}

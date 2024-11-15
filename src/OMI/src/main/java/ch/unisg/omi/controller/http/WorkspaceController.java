package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.port.in.BroadcastUseCase;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.SchemeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final GroupUseCase groupUseCase;
    private final SchemeUseCase schemeUseCase;
    private final BroadcastUseCase broadcastUseCase;

    @PostMapping(path = "/workspaces")
    public ResponseEntity<String> newWorkspace(@RequestBody String requestBody) {

        System.out.println("New workspace is created.");

        // TODO: Change the group name to the location
        groupUseCase.addGroup("room1-group");

        // TODO: Make the scheme dynamic
        schemeUseCase.startScheme("room1-scheme");

        // TODO: Broadcast dynamically
        broadcastUseCase.broadcast("room1-group");

        return ResponseEntity.ok().body("OK");
    }
}

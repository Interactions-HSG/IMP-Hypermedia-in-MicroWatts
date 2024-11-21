package ch.unisg.omi.controller.http;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArtifactController {

    @PostMapping(path = "/workspaces/artifacts")
    public ResponseEntity<String> newArtifact(@RequestBody String requestBody) {

        System.out.println("New artifact is created.");

        // TODO: Retrieve the agent id

        // TODO: Add agent to the agent list of the organization

        // TODO: Send request to the agent with the group name

        return ResponseEntity.ok().body("OK");
    }

}

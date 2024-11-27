package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.AgentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArtifactController {

    private final AgentUseCase agentUseCase;

    @PostMapping(path = "/workspaces/artifacts")
    public ResponseEntity<String> newArtifact(
            @RequestHeader("Location") String location,
            @RequestBody String requestBody
    ) {

        System.out.println("New artifact is created.");

        System.out.println(location); // does not work right now

        // TODO: Retrieve the agent id from the location header
        String agentId = "";

        agentUseCase.addAgent(agentId);

        return ResponseEntity.ok().body("OK");
    }

}

package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.port.in.AgentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AgentController {

    private final AgentUseCase agentUseCase;

    @PostMapping(path = "/agents")
    public ResponseEntity<Void> joinOrganization(@RequestBody String agentName) {

        agentUseCase.addAgent(agentName);

        return new ResponseEntity("Hello World", HttpStatus.OK);
    }

    @GetMapping(path = "/agents")
    public ResponseEntity<String> getAgentById(@RequestParam String id) {
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}

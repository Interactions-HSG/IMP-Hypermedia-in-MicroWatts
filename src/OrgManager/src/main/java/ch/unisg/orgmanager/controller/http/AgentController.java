package ch.unisg.orgmanager.controller.http;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgentController {

    @PostMapping(path = "/agents")
    public ResponseEntity<Void> addAgent() {
        return new ResponseEntity("Hello World", HttpStatus.OK);
    }
}

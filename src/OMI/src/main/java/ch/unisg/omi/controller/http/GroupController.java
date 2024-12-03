package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupUseCase groupUseCase;

    @PostMapping(path="/room1")
    public ResponseEntity<String> adoptRole(@RequestBody Object test) {

        RoleCommand command = new RoleCommand("", "", "");

        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }
    // TODO: When an agent joins the group, send all available roles.
}

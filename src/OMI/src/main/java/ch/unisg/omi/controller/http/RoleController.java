package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleUseCase roleUseCase;

    // TODO: Add param for role id to allow agents to adopt a role.
    @PostMapping(path = "/roles")
    public ResponseEntity<String> addRole(@RequestBody String test) {

        RoleCommand command = new RoleCommand("","","");

        roleUseCase.adoptRole(command);

        return new ResponseEntity("Hello World!", HttpStatus.OK);
    }
}

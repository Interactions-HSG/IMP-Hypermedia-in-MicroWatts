package ch.unisg.omi.controller.http;

import ch.unisg.omi.controller.dto.RoleDTO;
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

    @PostMapping(path = "/roles")
    public ResponseEntity<String> addRole(
            @RequestBody RoleDTO roleDTO
    ) {

        RoleCommand command = new RoleCommand(
                roleDTO.getAgentId(),
                roleDTO.getRoleId(),
                roleDTO.getGroupId()
        );

        roleUseCase.adoptRole(command);

        return new ResponseEntity("Hello World!", HttpStatus.OK);
    }
}

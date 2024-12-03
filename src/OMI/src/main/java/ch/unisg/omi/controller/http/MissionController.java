package ch.unisg.omi.controller.http;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MissionController {

    private final MissionUseCase missionUseCase;

    @PostMapping(path = "/missions")
    public ResponseEntity<String> commitToMission(
            @RequestBody MissionDTO missionDTO
            ) {

        // TODO: Commit to mission
        MissionCommand command = new MissionCommand(
                missionDTO.getAgentId(),
                missionDTO.getMissionId(),
                missionDTO.getSchemeId()
        );

        missionUseCase.commitMission(command);

        return ResponseEntity.ok().body("Mission committed");
    }
}

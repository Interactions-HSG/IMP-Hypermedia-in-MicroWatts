package ch.unisg.omi.controller.http;

import ch.unisg.omi.controller.dto.MissionDTO;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionController.class);
    private final MissionUseCase missionUseCase;

    @PostMapping(path = "/missions")
    public ResponseEntity<String> commitToMission(
            @RequestBody MissionDTO missionDTO
            ) {

        MissionCommand command = new MissionCommand(
                missionDTO.getAgentId(),
                missionDTO.getMissionId(),
                missionDTO.getSchemeId(),
                missionDTO.getGoalId()
        );


        LOGGER.info("Mission command: {}", command);
        missionUseCase.commitMission(command);

        return ResponseEntity.ok().body("Mission committed");
    }
}

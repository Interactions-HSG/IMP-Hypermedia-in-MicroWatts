package ch.unisg.omi.core.service;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.MissionUseCase;
import ch.unisg.omi.core.port.in.command.MissionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("mission")
public class MissionService implements MissionUseCase {

    private final CoapServerConfig server = CoapServerConfig.getInstance();
    private final Organization organization = Organization.getOrganization();

    public void commitMission(MissionCommand command) {
        try {
            organization
                    .getOrgEntity()
                    .getAgent(command.getAgentName())
                    .commitToMission(command.getMissionName(), command.getSchemeName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

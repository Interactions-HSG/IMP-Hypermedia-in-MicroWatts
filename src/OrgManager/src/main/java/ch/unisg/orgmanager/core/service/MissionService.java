package ch.unisg.orgmanager.core.service;

import ch.unisg.orgmanager.config.CoapServerConfig;
import ch.unisg.orgmanager.core.entity.Organization;
import ch.unisg.orgmanager.core.port.in.MissionUseCase;
import ch.unisg.orgmanager.core.port.in.command.MissionCommand;

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

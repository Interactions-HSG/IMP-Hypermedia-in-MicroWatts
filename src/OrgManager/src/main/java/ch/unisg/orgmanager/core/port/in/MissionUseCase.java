package ch.unisg.orgmanager.core.port.in;

import ch.unisg.orgmanager.core.port.in.command.MissionCommand;

public interface MissionUseCase {
    void commitMission(MissionCommand command);
}

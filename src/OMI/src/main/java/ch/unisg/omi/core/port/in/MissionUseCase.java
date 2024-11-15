package ch.unisg.omi.core.port.in;

import ch.unisg.omi.core.port.in.command.MissionCommand;

public interface MissionUseCase {
    void commitMission(MissionCommand command);
}

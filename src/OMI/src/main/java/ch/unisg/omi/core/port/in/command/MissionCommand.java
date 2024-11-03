package ch.unisg.omi.core.port.in.command;

import lombok.Value;

@Value
public class MissionCommand {

    private final String agentName;

    private final String missionName;

    private final String schemeName;

    public MissionCommand(String agentName, String missionName, String schemeName) {
        this.agentName = agentName;
        this.missionName = missionName;
        this.schemeName = schemeName;
    }
}

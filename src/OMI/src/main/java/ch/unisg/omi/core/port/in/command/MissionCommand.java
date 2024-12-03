package ch.unisg.omi.core.port.in.command;

import lombok.Getter;
import lombok.Value;

@Value
public class MissionCommand {

    @Getter
    private final String agentId;

    @Getter
    private final String missionId;

    @Getter
    private final String schemeId;


    public MissionCommand(String agentId, String missionId, String schemeId) {
        this.agentId = agentId;
        this.missionId = missionId;
        this.schemeId = schemeId;
    }
}

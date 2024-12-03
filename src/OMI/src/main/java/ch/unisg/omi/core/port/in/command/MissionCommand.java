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

    @Getter
    private final String goalId;


    public MissionCommand(String agentId, String missionId, String schemeId, String goalId) {
        this.agentId = agentId;
        this.missionId = missionId;
        this.schemeId = schemeId;
        this.goalId = goalId;
    }
}

package ch.unisg.omi.core.port.out;

import moise.oe.OEAgent;
import moise.os.fs.Goal;

public interface AgentPort {
    void sendGroupName(String agentId, String groupName);
    void sendRoles(String agentId, Object[] roles);
    void sendGoal(OEAgent agentId, Goal goal);
}

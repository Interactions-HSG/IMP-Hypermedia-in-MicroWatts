package ch.unisg.omi.core.port.out;

import moise.oe.OEAgent;
import moise.oe.SchemeInstance;
import moise.os.fs.Goal;
import moise.os.fs.Mission;

public interface AgentPort {
    void sendGroupName(String agentId, String groupName);
    void sendRoles(String agentId, Object[] roles);
    void sendGoal(OEAgent agentId, Goal goal, String groupId, Mission mission, SchemeInstance scheme);
    void notifyGoal(OEAgent agentId, Goal goal, String groupId, Mission mission, SchemeInstance scheme);
}

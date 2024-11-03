package ch.unisg.omi.core.port.in;

import moise.oe.OEAgent;

import java.util.List;

public interface AgentUseCase {
    void addAgent(String agentName);
    List<OEAgent> getAgents();
    void removeAgent(String agentName);
}

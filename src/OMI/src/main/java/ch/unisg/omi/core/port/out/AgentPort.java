package ch.unisg.omi.core.port.out;

public interface AgentPort {
    void sendGroupName(String agentId, String groupName);
    void sendRoles(String agentId, Object[] roles);
}

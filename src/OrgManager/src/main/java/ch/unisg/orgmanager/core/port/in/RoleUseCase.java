package ch.unisg.orgmanager.core.port.in;

public interface RoleUseCase {
    void adoptRole(String agentName, String roleName);
    String getRoles();
}

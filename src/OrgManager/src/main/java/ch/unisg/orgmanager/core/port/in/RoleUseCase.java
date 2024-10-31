package ch.unisg.orgmanager.core.port.in;

import ch.unisg.orgmanager.core.port.in.command.RoleCommand;

public interface RoleUseCase {
    void adoptRole(RoleCommand command);
    String getRoles();
}

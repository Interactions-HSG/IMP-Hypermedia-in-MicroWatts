package ch.unisg.omi.core.port.in;

import ch.unisg.omi.core.port.in.command.RoleCommand;

public interface RoleUseCase {
    void adoptRole(RoleCommand command);
    String getRoles();
}

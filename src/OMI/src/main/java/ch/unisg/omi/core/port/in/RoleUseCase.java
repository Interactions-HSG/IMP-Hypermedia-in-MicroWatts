package ch.unisg.omi.core.port.in;

import ch.unisg.omi.core.port.in.command.RoleCommand;

public interface RoleUseCase {
    int adoptRole(RoleCommand command);
}

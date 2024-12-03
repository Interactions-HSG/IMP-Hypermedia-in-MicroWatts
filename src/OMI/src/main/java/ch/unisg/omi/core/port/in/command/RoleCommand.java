package ch.unisg.omi.core.port.in.command;

import lombok.NonNull;
import lombok.Value;

@Value
public class RoleCommand {

    @NonNull
    private final String agentId;

    @NonNull
    private final String roleId;

    @NonNull
    private final String groupId;

    public RoleCommand(
            String agentId,
            String roleId,
            String groupId
    ) {
        this.agentId = agentId;
        this.roleId = roleId;
        this.groupId = groupId;
    }
}

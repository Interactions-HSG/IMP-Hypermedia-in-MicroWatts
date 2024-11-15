package ch.unisg.omi.core.port.in.command;

import lombok.NonNull;
import lombok.Value;

@Value
public class RoleCommand {

    @NonNull
    private final String agentName;

    @NonNull
    private final String roleName;

    @NonNull
    private final String groupName;

    public RoleCommand(
            String agentName,
            String roleName,
            String groupName
    ) {
        this.agentName = agentName;
        this.roleName = roleName;
        this.groupName = groupName;
    }
}

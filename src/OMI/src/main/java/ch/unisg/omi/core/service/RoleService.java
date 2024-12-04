package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import lombok.RequiredArgsConstructor;
import moise.oe.RolePlayer;
import moise.os.ss.Role;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {

    private Organization organization = Organization.getOrganization();

    @Override
    public void adoptRole(RoleCommand command) {

        try {

            RolePlayer rolePlayer = organization
                    .getOrgEntity()
                    .getAgent(command.getAgentId())
                    .adoptRole(
                            command.getRoleId(),
                            command.getGroupId()
                    );

            organization.getOrgEntity().findGroup(command.getGroupId()).addPlayer(rolePlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getRoles() {
        // TODO: Implement get roles
        Collection<Role> roles = organization.getOrgEntity().getOS().getSS().getRolesDef();
    }
}

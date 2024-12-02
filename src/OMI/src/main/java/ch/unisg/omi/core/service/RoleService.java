package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.RoleUseCase;
import ch.unisg.omi.core.port.in.command.RoleCommand;
import lombok.RequiredArgsConstructor;
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

            organization
                    .getOrgEntity()
                    .getAgent(command.getAgentName())
                    .adoptRole(
                            command.getRoleName(),
                            command.getGroupName()
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getRoles() {
        Collection<Role> roles = organization.getOrgEntity().getOS().getSS().getRolesDef();
        return String.valueOf(roles);
    }
}

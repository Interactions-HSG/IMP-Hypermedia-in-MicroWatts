package ch.unisg.orgmanager.core.service;

import ch.unisg.orgmanager.core.entity.Organization;
import ch.unisg.orgmanager.core.port.in.RoleUseCase;
import lombok.RequiredArgsConstructor;
import moise.oe.RolePlayer;
import moise.xml.DOMUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {

    private Organization organization = Organization.getOrganization();

    @Override
    public void adoptRole(String agentName, String roleName) {

        try {

            System.out.println(organization.getOrgEntity().getAgents());

            System.out.println(agentName + " " + roleName);

            System.out.println(organization.getOrgEntity().getGroups());
            System.out.println(organization.getOrgEntity().getGroups().toString());
            RolePlayer rolePlayer = organization.getOrgEntity().getAgent(agentName).adoptRole(roleName, "group_room_automation");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getRoles() {
        String roles = organization.getOrgEntity().getOS().getSS().getRolesDef().toString();
        return roles;
    }
}

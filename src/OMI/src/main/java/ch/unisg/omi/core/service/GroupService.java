package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.out.GroupPort;
import lombok.RequiredArgsConstructor;
import moise.oe.GroupInstance;
import moise.os.CardinalitySet;
import moise.os.ss.Role;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service("Group")
public class GroupService implements GroupUseCase {

    private final Organization organization = Organization.getOrganization();
    private final GroupPort groupPort;

    @Override
    public void addGroup(String groupName) {

        try {
            // add a new group to the organization
            GroupInstance group = organization.getOrgEntity().addGroup(groupName, "monitoring_team");

            // get all roles from the group
            CardinalitySet<Role> roles = organization.getOrgEntity().findGroup(group.getId()).getGrSpec().getRoles();

            // update the group resource with all roles
            groupPort.updateRoles(group.getId(), roles);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getGroups() {
        return organization.getOrgEntity().getGroups().toString();
    }
}

package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.out.GroupPort;
import lombok.RequiredArgsConstructor;
import moise.common.MoiseConsistencyException;
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

    @Override
    public void removeGroup(String groupName) {
        try {
            final var group = organization.getOrgEntity().findGroup(groupName);
            final var players = group.getPlayers();
            players.forEach(group::removePlayer);
            organization.getOrgEntity().removeGroup(groupName);
        } catch (MoiseConsistencyException e) {
            e.printStackTrace();
        }
    }

    public String getGroups() {
        return organization.getOrgEntity().getGroups().toString();
    }
}

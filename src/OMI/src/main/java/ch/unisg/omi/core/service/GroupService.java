package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Broadcaster;
import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.GroupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("Group")
public class GroupService implements GroupUseCase {

    private Organization organization = Organization.getOrganization();

    @Override
    public void addGroup(String groupName) {

        try {
            organization.getOrgEntity().addGroup(groupName, "group_room_automation");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getGroups() {
        return organization.getOrgEntity().getGroups().toString();
    }
}

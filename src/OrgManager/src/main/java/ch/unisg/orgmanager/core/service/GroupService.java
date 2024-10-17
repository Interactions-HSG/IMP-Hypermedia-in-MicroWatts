package ch.unisg.orgmanager.core.service;

import ch.unisg.orgmanager.core.entity.Organization;
import ch.unisg.orgmanager.core.port.in.GroupUseCase;

public class GroupService implements GroupUseCase {

    private Organization organization = Organization.getOrganization();

    public void addGroup(String groupName) {

        try {
            organization.getOrgEntity().addGroup(groupName);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getGroups() {
        return organization.getOrgEntity().getGroups().toString();
    }
}

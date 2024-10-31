package ch.unisg.orgmanager.core.service;

import ch.unisg.orgmanager.core.entity.Organization;
import ch.unisg.orgmanager.core.port.in.SchemeUseCase;

public class SchemeService implements SchemeUseCase {

    private Organization organization = Organization.getOrganization();

    public void startScheme(String schemeName) {

        try {
            organization.getOrgEntity().startScheme(schemeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addScheme() {
        try {
            // TODO: Add scheme to the group represented by the group id
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

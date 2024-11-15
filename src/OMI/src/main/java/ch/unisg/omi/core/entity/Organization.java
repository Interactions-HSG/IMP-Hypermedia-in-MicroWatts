package ch.unisg.omi.core.entity;

import moise.oe.OE;

public class Organization {

    private OE orgEntity;

    private static final Organization organization = new Organization("smartBuilding", "org.xml");

    private Organization(String name, String uri) {
        try {
            this.orgEntity = OE.createOE(name, uri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Organization getOrganization() {
        return organization;
    }

    public OE getOrgEntity() {
        return orgEntity;
    }

}

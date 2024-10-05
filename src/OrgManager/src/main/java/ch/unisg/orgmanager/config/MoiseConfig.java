package ch.unisg.orgmanager.config;

import moise.oe.OE;

public class MoiseConfig {
    OE orgEntity;

    public MoiseConfig(String uri) {
        try {
            orgEntity = OE.createOE("smartBuilding", uri);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

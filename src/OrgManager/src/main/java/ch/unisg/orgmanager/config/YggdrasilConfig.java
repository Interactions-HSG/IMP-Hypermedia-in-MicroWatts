package ch.unisg.orgmanager.config;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;

import java.io.IOException;
import java.util.Optional;

public class YggdrasilConfig {

    public static String ENTRYPOINT = "http://yggdrasil:8080/";
    public static String WORKSPACE = "root";
    public static String NAME = "OrgManager";

    public YggdrasilConfig() {
        try {
            ThingDescription tdYggdrasil = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT);

            ThingDescription tdWorkspace = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, ENTRYPOINT + "workspaces/" + WORKSPACE);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}

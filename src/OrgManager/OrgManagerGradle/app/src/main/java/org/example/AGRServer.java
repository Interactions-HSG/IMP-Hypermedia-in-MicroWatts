package org.example;

import org.eclipse.californium.core.CoapServer;
import org.example.entities.Group;
import org.example.entities.GroupRole;

public class AGRServer extends CoapServer{

    public AGRServer(int port) {
        super(port);
        Group group = new Group("room1");
        add(group);
        System.out.printf("Server is listening on port %d\n", port);
    }
}

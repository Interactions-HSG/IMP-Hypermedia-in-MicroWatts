package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.controller.coap.RolesResource;
import ch.unisg.omi.core.port.out.GroupPort;
import lombok.RequiredArgsConstructor;
import moise.os.CardinalitySet;
import moise.os.ss.Role;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {

    CoapServerConfig server = CoapServerConfig.getInstance();

    public void updateRoles(String groupId, CardinalitySet<Role> roleList) {
        var roles = (RolesResource) server.getRoot().getChild(groupId).getChild("roles");
        roles.addRoles(roleList.getAll());
        roles.notifyResource();
    }
}

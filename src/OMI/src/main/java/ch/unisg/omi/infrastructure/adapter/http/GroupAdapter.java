package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.controller.coap.GroupResource;
import ch.unisg.omi.core.port.in.GroupUseCase;
import ch.unisg.omi.core.port.out.GroupPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {

    CoapServerConfig server = CoapServerConfig.getInstance();
    private final GroupUseCase groupUseCase;

    @Override
    public void createGroupResource(String groupId) {

        server.add(new GroupResource(groupId, groupUseCase));
    }
}

package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Broadcaster;
import ch.unisg.omi.core.port.in.BroadcastUseCase;
import ch.unisg.omi.core.port.out.AgentPort;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service("Broadcast")
public class BroadcastService implements BroadcastUseCase {

    private final AgentPort agentPort;
    private final MissionService missionService;
    private HashMap<String, Broadcaster> broadcasterHashMap = new HashMap<>();

    @Override
    public void broadcast(String groupName) {
        final var b = new Broadcaster(agentPort, missionService);
        b.setGroupName(groupName);
        broadcasterHashMap.put(groupName, b);
        b.start();
    }

    @Override
    public void removeBroadcaster(String groupName) {
        final var b = broadcasterHashMap.remove(groupName);
        b.shutdown();
    }

    @Async
    public void Test() {

    }
}

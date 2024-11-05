package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Broadcaster;
import ch.unisg.omi.core.port.in.BroadcastUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service("Broadcast")
public class BroadcastService implements BroadcastUseCase {

    private final Broadcaster broadcaster;

    @Override
    public void broadcast(String groupName) {
        try {
            broadcaster.send(groupName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Async
    public void Test() {

    }
}

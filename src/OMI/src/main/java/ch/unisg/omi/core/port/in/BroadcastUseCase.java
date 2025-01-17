package ch.unisg.omi.core.port.in;

public interface BroadcastUseCase {
    void broadcast(String groupName);
    void removeBroadcaster(String groupName);
}

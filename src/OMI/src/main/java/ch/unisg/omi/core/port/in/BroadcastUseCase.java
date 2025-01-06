package ch.unisg.omi.core.port.in;

public interface BroadcastUseCase {
    public void broadcast(String groupName);
    public void removeBroadcaster(String groupName);
}

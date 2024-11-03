package ch.unisg.omi.core.entity;

public class Broadcaster {

    private static final Broadcaster broadcaster = new Broadcaster();

    private final Organization organization = Organization.getOrganization();

    private Broadcaster() {}

    public static Broadcaster getBroadcaster() {
        return broadcaster;
    }

    public void send() {
        System.out.println("Broadcast Roles to Groups");
    }
}

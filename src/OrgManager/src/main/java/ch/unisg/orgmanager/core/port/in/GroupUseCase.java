package ch.unisg.orgmanager.core.port.in;

public interface GroupUseCase {
    public void addGroup(String groupName);
    public String getGroups();
}

package ch.unisg.omi.core.port.in;

import moise.os.CardinalitySet;
import moise.os.ss.Role;

public interface GroupUseCase {
    public void addGroup(String groupName);
    public String getGroups();
}

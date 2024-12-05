package ch.unisg.omi.core.port.out;

import moise.os.CardinalitySet;
import moise.os.ss.Role;

public interface GroupPort {
    void updateRoles(String groupId, CardinalitySet<Role> roleList);
}

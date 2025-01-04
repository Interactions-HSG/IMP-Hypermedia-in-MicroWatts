package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.AgentUseCase;
import ch.unisg.omi.core.port.out.AgentPort;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import moise.common.MoiseConsistencyException;
import moise.oe.OEAgent;
import moise.oe.RolePlayer;
import moise.os.ss.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("Agent")
public class AgentService implements AgentUseCase {

    private static final Organization organization = Organization.getOrganization();
    private final AgentPort agentPort;

    @Override
    public void addAgent(String agentId) {


        try {
            organization.getOrgEntity().addAgent(agentId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<OEAgent> getAgents() {
        try {
            List<OEAgent> agents = organization.getOrgEntity().getAgents().stream().toList();

            return agents;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeAgent(String agentName) {
        try {
            // Create a copy of the roles to avoid ConcurrentModificationException
            ArrayList<RolePlayer> roles =
                new ArrayList(organization.getOrgEntity().getAgent(agentName).getRoles());

            // Iterate over the copied list
            for (RolePlayer role : roles) {
                try {
                    organization.getOrgEntity().getAgent(agentName).abortRole(role);
                } catch (MoiseConsistencyException e) {
                    System.out.println("[AgentService] Error:" + e);
                    throw new RuntimeException(e);
                }
            }

            // Remove the agent after aborting all roles
            organization.getOrgEntity().removeAgent(agentName, true);
        } catch (Exception e) {
            System.out.println("[AgentService] Error:" + e);
            e.printStackTrace();
        }
    }
}

package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.AgentUseCase;
import lombok.RequiredArgsConstructor;
import moise.oe.OEAgent;
import moise.oe.RolePlayer;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("Agent")
public class AgentService implements AgentUseCase {

    private static final Organization organization = Organization.getOrganization();


    public void addAgent(String agentName) {
        try {
            organization.getOrgEntity().addAgent(agentName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<OEAgent> getAgents() {
        try {
            List<OEAgent> agents = organization.getOrgEntity().getAgents().stream().toList();

            return agents;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeAgent(String agentName) {
        try {
            organization.getOrgEntity().removeAgent(agentName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

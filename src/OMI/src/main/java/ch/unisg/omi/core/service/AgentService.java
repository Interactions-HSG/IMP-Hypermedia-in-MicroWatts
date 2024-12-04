package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.AgentUseCase;
import ch.unisg.omi.core.port.out.AgentPort;
import lombok.RequiredArgsConstructor;
import moise.oe.OEAgent;
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

            // add agent to the organization
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
            organization.getOrgEntity().removeAgent(agentName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.AgentUseCase;
import ch.unisg.omi.core.port.out.AgentPort;
import lombok.RequiredArgsConstructor;
import moise.oe.OEAgent;
import moise.oe.RolePlayer;
import org.springframework.stereotype.Component;
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

            // TODO: retrieve the agentName from the agentId
            String agentName = agentId;

            // TODO: Add agent to the agent list of the organization
            String workspaceName = "room11"; // Retrieve the workspace name from the location header

            organization.getOrgEntity().addAgent(agentName);

            agentPort.sendGroupName(workspaceName + "-group");

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

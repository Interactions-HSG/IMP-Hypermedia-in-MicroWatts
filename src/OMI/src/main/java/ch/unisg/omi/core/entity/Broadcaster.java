package ch.unisg.omi.core.entity;

import ch.unisg.omi.core.port.out.AgentPort;
import lombok.RequiredArgsConstructor;
import moise.common.MoiseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class Broadcaster {

    private final Organization organization = Organization.getOrganization();
    private final AgentPort agentPort;


    @Async
    public CompletableFuture<String> send(String groupName) throws InterruptedException {

        System.out.println("Broadcaster: " + groupName);

        // While the group is not well-formed, broadcast roles and group.
        // ?formationStatus == false
        while (!organization.getOrgEntity().findGroup(groupName).isWellFormed()) {

            System.out.println("Waiting for broadcast: " + groupName);

            Object[] roles = organization.getOrgEntity().findGroup(groupName).getGrSpec().getRoles().getAll().toArray();

            organization.getOrgEntity().getAgents().forEach(agent -> {
                agentPort.sendGroupName(agent.toString(), groupName);
                agentPort.sendRoles(agent.toString(), roles);
            });

            // Sleep for 5 seconds, broadcast again
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Group " + groupName + " is well formed.");

        try {
            organization.getOrgEntity().startScheme("monitoring_scheme");
        } catch (MoiseException eMoise) {
            eMoise.printStackTrace();
        }


        // ?formationStatus == true

        // TODO: Next while loop to "achieve" plans for all missionplayer?

        organization.getOrgEntity().findGroup("monitoring_team").getPlayers().forEach(player -> {
            System.out.println("Role player: " + player);
        });

        organization.getOrgEntity().findScheme("monitoring_scheme").getPlayers().forEach(player -> {
            System.out.println("Mission player: " + player);
        });

        return CompletableFuture.completedFuture("group is well-formed.");
    }
}

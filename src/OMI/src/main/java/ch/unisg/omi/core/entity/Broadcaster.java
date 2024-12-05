package ch.unisg.omi.core.entity;

import ch.unisg.omi.core.port.out.AgentPort;
import ch.unisg.omi.core.service.MissionService;
import lombok.RequiredArgsConstructor;
import moise.common.MoiseException;
import moise.oe.SchemeInstance;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class Broadcaster {

    private final Organization organization = Organization.getOrganization();
    private final AgentPort agentPort;
    private final MissionService mission;

    @Async
    public CompletableFuture<String> send(String groupName) throws InterruptedException {

        System.out.println("Broadcaster: " + groupName);

        // While the group is not well-formed, broadcast roles and group.
        // ?formationStatus == false
        while (!organization.getOrgEntity().findGroup(groupName).isWellFormed()) {

            System.out.println("Waiting for broadcast: " + groupName);

            Object[] roles = organization.getOrgEntity().findGroup(groupName).getGrSpec().getRoles().getAll().toArray();

            // Send all agents the available group names and roles
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

        // ?formationStatus == true
        System.out.println("Log: Group " + groupName + " is well formed.");

        /*
         * Start new scheme
         */
        try {

            SchemeInstance schemeInstance = organization.getOrgEntity().startScheme("monitoring_scheme");
            schemeInstance.addResponsibleGroup(groupName);


            organization.getOrgEntity().findGroup(groupName).getPlayers().forEach((player) -> {

                player.getPermissions().forEach(permission -> {

                    permission.getMission().getGoals().forEach(goal -> {

                        agentPort.sendGoal(player.getPlayer(), goal, groupName, permission.getMission(), permission.getScheme());
                        agentPort.notifyGoal(player.getPlayer(), goal, groupName, permission.getMission(), permission.getScheme());

                    });
                });

            });

            // TODO: What next?
            //organization.getOrgEntity().finishScheme(schemeInstance);

        } catch (MoiseException eMoise) {
            eMoise.printStackTrace();
        }

        return CompletableFuture.completedFuture("Finished.");
    }
}

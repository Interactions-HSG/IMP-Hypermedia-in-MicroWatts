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

        boolean wasWellFormed = false; // Track the previous state of the group

        while (true) {
            boolean isWellFormed = organization.getOrgEntity().findGroup(groupName).isWellFormed();

            if (isWellFormed) {
                if (!wasWellFormed) { // Perform actions only on transition to well-formed
                    System.out.println("Log: Group " + groupName + " is well formed.");
                    try {
                        // Start new scheme
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

                        // Optionally finish the scheme later
                        // organization.getOrgEntity().finishScheme(schemeInstance);

                    } catch (MoiseException eMoise) {
                        eMoise.printStackTrace();
                    }
                }
            } else {
                if (wasWellFormed) { // Perform actions only on transition to not well-formed
                    System.out.println("Log: Group " + groupName + " is no longer well formed.");
                    // need to stop and remove the scheme
                    organization.getOrgEntity().findInstancesOfSchSpec("monitoring_scheme").forEach(schemeInstance -> {
                      try {
                        organization.getOrgEntity().finishScheme(schemeInstance);
                      } catch (MoiseException e) {
                        throw new RuntimeException(e);
                      }
                    });
                }

                // Broadcast roles and group
                System.out.println("Waiting for broadcast: " + groupName);

                Object[] roles = organization.getOrgEntity().findGroup(groupName).getGrSpec().getRoles().getAll().toArray();

                organization.getOrgEntity().getAgents().forEach(agent -> {
                    agentPort.sendGroupName(agent.toString(), groupName);
                    agentPort.sendRoles(agent.toString(), roles);
                });
            }

            // Update the previous state
            wasWellFormed = isWellFormed;

            // Sleep for 5 seconds before checking again
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break; // Exit the loop if the thread is interrupted
            }
        }

        return CompletableFuture.completedFuture("Monitoring stopped.");
    }
}

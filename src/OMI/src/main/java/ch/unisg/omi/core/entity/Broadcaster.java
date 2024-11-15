package ch.unisg.omi.core.entity;

import moise.common.MoiseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class Broadcaster {

    private final Organization organization = Organization.getOrganization();

    @Async
    public CompletableFuture<String> send(String groupName) throws InterruptedException {

        // While the group is not well-formed, broadcast roles and group.
        // ?formationStatus == false
        while (!organization.getOrgEntity().findGroup(groupName).isWellFormed()) {
            organization.getOrgEntity().getAgents().forEach(agent -> {
                // TODO: Implement a broadcast logic (send groupName, roleName -> Role Adapter needs agentName, groupName, roleName)
                System.out.println("Sending agent " + agent);
            });

            // Sleep for 5 seconds, broadcast again
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // ?formationStatus == true
        try {
            // TODO: Start scheme
            organization.getOrgEntity().startScheme("");
        } catch (MoiseException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture("group is well-formed.");
    }
}

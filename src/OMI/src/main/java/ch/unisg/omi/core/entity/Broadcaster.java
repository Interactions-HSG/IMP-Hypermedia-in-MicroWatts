package ch.unisg.omi.core.entity;

import ch.unisg.omi.core.port.out.AgentPort;
import ch.unisg.omi.core.service.MissionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moise.common.MoiseCardinalityException;
import moise.common.MoiseConsistencyException;
import moise.common.MoiseException;
import moise.oe.GoalInstance;
import moise.oe.MissionPlayer;
import moise.oe.OEAgent;
import moise.oe.RolePlayer;
import moise.oe.SchemeInstance;
import moise.os.fs.Goal;
import moise.os.fs.Mission;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class Broadcaster extends Thread {

  private final Organization organization = Organization.getOrganization();
  private final AgentPort agentPort;
  private final MissionService mission;
  @Setter
  private String groupName;


  @Override
  public void run() {
    System.out.println("[Broadcaster] Broadcaster: " + groupName);

    boolean wasWellFormed = false; // Track the previous state of the group
    boolean finishedScheme = false;

    SchemeInstance schemeInstance = null;
    try {
      schemeInstance = organization.getOrgEntity().startScheme("monitoring_scheme");
    } catch (MoiseException e) {
      System.out.println("[Broadcaster] Error: " + e);
    }
    while (true) {
      boolean isWellFormed = organization.getOrgEntity().findGroup(groupName).isWellFormed();

      if (isWellFormed) {
        if (!wasWellFormed) { // Perform actions only on transition to well-formed
          System.out.println("[Broadcaster] Group " + groupName + " is well formed.");

          try {
            schemeInstance.addResponsibleGroup(groupName);
          } catch (MoiseConsistencyException e) {
            throw new RuntimeException(e);
          }


          final var playersMap = new HashMap<RolePlayer, List<PlayerInfo>>();

          organization.getOrgEntity().findGroup(groupName).getPlayers().forEach((player) -> {
            player.getPermissions().forEach(permission -> {
              permission.getMission().getGoals().forEach(goal -> {
                final var agentId = player.getPlayer();
                final var missionId = permission.getMission();
                final var schemeId = permission.getScheme();
                playersMap.computeIfAbsent(player, (k) -> new ArrayList<>()).add(
                    new PlayerInfo(
                        agentId,
                        goal,
                        groupName,
                        missionId,
                        schemeId
                    )
                );
                agentPort.sendGoal(player.getPlayer(), goal, groupName, permission.getMission(),
                    permission.getScheme());
              });
            });
          });

          playersMap.forEach((player, playerInfos)
              -> agentPort.notifyGoal(playerInfos));
          // Optionally finish the scheme later
          // organization.getOrgEntity().finishScheme(schemeInstance);

        } else {
          // Check if scheme is well-formed
          final var schemeWellFormed = schemeInstance.isWellFormed();
          System.out.println("[Broadcaster] Schemes: " + organization.getOrgEntity().getSchemes().size());

          System.out.println("[Broadcaster] Schemes: " + organization.getOrgEntity().getSchemes());
          if (schemeWellFormed && !finishedScheme) {
            final var g = schemeInstance.getRoot();

            // schemeInstance.remResponsibleGroup(groupName);
            // organization.getOrgEntity().finishScheme(schemeInstance);
            // finishedScheme = true;

          } else {
            System.out.println("Log: Scheme is not well formed.");
          }
        }
      } else {
        if (wasWellFormed) { // Perform actions only on transition to not well-formed
          // TODO: Send Notifications to group members that group is no longer well formed
          System.out.println("[Broadcaster1] Group " + groupName + " is no longer well formed.");
          finishedScheme = false;

          final var groupMembers = organization.getOrgEntity().findGroup(groupName).getPlayers();
          groupMembers.forEach(gm -> agentPort.notifyGroup(gm.getPlayer(), groupName));

          // need to stop and remove the scheme
          SchemeInstance finalSchemeInstance = schemeInstance;
          try {
            final var players = new ArrayList<>(schemeInstance.getPlayers());
            players.forEach(mp -> {
              try {
                mp.getPlayer().abortMission(mp.getMission().getId(),
                    finalSchemeInstance);
              } catch (MoiseException e) {
                System.out.println("Error Moise: " + e);
                throw new RuntimeException(e);
              }
              catch (Exception e) {
                System.out.println("Error: " + e);
                throw new RuntimeException(e);
              }
            });
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }


          try {
            schemeInstance.remResponsibleGroup(groupName);
          } catch (MoiseConsistencyException e) {
            throw new RuntimeException(e);
          }
        }

        // Broadcast roles and group
        System.out.println("Waiting for broadcast: " + groupName);

        Object[] roles =
            organization.getOrgEntity().findGroup(groupName).getGrSpec().getRoles().getAll()
                .toArray();

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

    System.out.println("Monitoring stopped.");
  }

  public record PlayerInfo(OEAgent player, Goal goal, String groupId, Mission mission,
                           SchemeInstance scheme) {
  }
}

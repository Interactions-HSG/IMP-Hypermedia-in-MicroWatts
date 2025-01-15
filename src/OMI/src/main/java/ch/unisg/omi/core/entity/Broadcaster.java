package ch.unisg.omi.core.entity;

import ch.unisg.omi.core.port.out.AgentPort;
import ch.unisg.omi.core.service.MissionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moise.common.MoiseConsistencyException;
import moise.common.MoiseException;
import moise.oe.OEAgent;
import moise.oe.RolePlayer;
import moise.oe.SchemeInstance;
import moise.os.fs.Goal;
import moise.os.fs.Mission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class Broadcaster extends Thread {
  private static final Logger LOGGER = LoggerFactory.getLogger(Broadcaster.class);


  private final Organization organization = Organization.getOrganization();
  private final AgentPort agentPort;
  private final MissionService mission;
  @Setter
  private String groupName;
  private boolean finishedScheme;
  private boolean wasWellFormed;
  private boolean running;


  public void shutdown() {
    running = false;
  }

  private void handleWellFormed(final SchemeInstance schemeInstance) {
    LOGGER.info("Group {} is well formed.", groupName);

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
  }

  private void handleNoLongerWellFormed(final SchemeInstance schemeInstance) {
    LOGGER.info("Group {} is no longer well formed.", groupName);
    finishedScheme = false;

    final var groupMembers = organization.getOrgEntity().findGroup(groupName).getPlayers();
    groupMembers.forEach((gm) -> {
      agentPort.notifyGroup(gm.getPlayer(), groupName);
    });

    // need to stop and remove the scheme
    SchemeInstance finalSchemeInstance = schemeInstance;
    try {
      final var players = new ArrayList<>(schemeInstance.getPlayers());
      players.forEach(mp -> {
        try {
          mp.getPlayer().abortMission(mp.getMission().getId(),
              finalSchemeInstance);
        } catch (Exception e) {
          LOGGER.error("Error aborting mission", e);
          throw new RuntimeException(e);
        }
      });
    } catch (Exception e) {
      LOGGER.error("Error aborting mission", e);
      throw new RuntimeException(e);
    }

    try {
      schemeInstance.remResponsibleGroup(groupName);
    } catch (MoiseConsistencyException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    Thread.currentThread().setName("Broadcaster-" + groupName);

    LOGGER.info("Started Broadcaster for: {}", groupName);

    wasWellFormed = false;
    finishedScheme = false;
    running = true;
    SchemeInstance schemeInstance = null;

    try {
      schemeInstance = organization.getOrgEntity().startScheme("monitoring_scheme");
    } catch (MoiseException e) {
      LOGGER.error("Error starting scheme", e);
    }

    while (running) {
      boolean isWellFormed = organization.getOrgEntity().findGroup(groupName).isWellFormed();

      if (isWellFormed) {
        if (!wasWellFormed) { // Perform actions only on transition to well-formed
          handleWellFormed(schemeInstance);
        } else {
          final var schemeWellFormed = schemeInstance.isWellFormed();
          LOGGER.info("Scheme is well formed: {}", schemeWellFormed);
        }
      } else {
        if (wasWellFormed) { // Perform actions only on transition to not well-formed
          handleNoLongerWellFormed(schemeInstance);
        }

        // Broadcast roles and group
        LOGGER.info("Broadcasting group and roles");

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

    LOGGER.info("Stopped Broadcaster");
  }

  public record PlayerInfo(OEAgent player, Goal goal, String groupId, Mission mission,
                           SchemeInstance scheme) {
  }
}

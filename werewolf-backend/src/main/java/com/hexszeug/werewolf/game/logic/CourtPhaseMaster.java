package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.controller.CourtController;
import com.hexszeug.werewolf.game.events.phase.PhaseEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class CourtPhaseMaster {
    private static final long TIMEOUT_ACCUSATION_SECONDS = 60;
    private static final long TIMEOUT_COURT_SECONDS = 30;

    private final TaskScheduler taskScheduler;
    private final NarrationService narrationService;
    private final KillingService killingService;

    private final Map<Village, ScheduledFuture<?>> courtTasks = new HashMap<>();

    @EventListener
    public void handlePhaseEvent(PhaseEvent event) {
        Village village = event.getVillage();
        if (event.getPhase() == Phase.ACCUSATION) {
            taskScheduler.schedule(
                    () -> accusationTimerTask(village),
                    Instant.now().plusSeconds(TIMEOUT_ACCUSATION_SECONDS)
            );
        } else if (event.getPhase() == Phase.COURT) {
            courtTasks.put(
                    village,
                    taskScheduler.schedule(
                            () -> courtTimerTask(village),
                            Instant.now().plusSeconds(TIMEOUT_COURT_SECONDS)
                    )
            );
        }
    }

    public void skipCourtTimer(Village village) {
        if (!courtTasks.containsKey(village)) {
            throw new IllegalStateException("The village has no running court timer.");
        }
        ScheduledFuture<?> task = courtTasks.get(village);
        if (task.isDone() || task.isCancelled()) {
            throw new IllegalStateException("The court timer task was not properly cleared up.");
        }
        task.cancel(false);
        if (task.isCancelled()) {
            courtTimerTask(village);
        }
    }

    private void accusationTimerTask(Village village) {
        narrationService.continueNarration(village, Phase.ACCUSATION);
    }

    private void courtTimerTask(Village village) {
        courtTasks.remove(village);
        Map<Player, Integer> votes = new HashMap<>();
        village.getPlayerList().forEach(player -> {
            Player vote = getVote(player, village);
            votes.compute(vote, (__, count) -> count == null ? 1 : count + 1);
            player.delete(CourtController.KEY_ACCUSATION);
            player.delete(CourtController.KEY_VOTE);
        });
        if (!votes.isEmpty()) {
            Set<Player> targets = new HashSet<>();
            AtomicInteger max = new AtomicInteger(0);
            votes.forEach((player, count) -> {
                if (count > max.get()) {
                    max.set(count);
                    targets.clear();
                }
                if (count == max.get()) {
                    targets.add(player);
                }
            });
            targets
                    .stream()
                    .skip(new Random().nextInt(targets.size()))
                    .findFirst()
                    .ifPresent(target -> killingService.kill(target, DeathReason.EXECUTED));
        }
        narrationService.continueNarration(village, Phase.COURT);
    }

    private Player getVote(Player player, Village village) {
        try {
            return village.getPlayerById(player.get(CourtController.KEY_VOTE, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Vote contains non string.", ex);
        }
    }
}

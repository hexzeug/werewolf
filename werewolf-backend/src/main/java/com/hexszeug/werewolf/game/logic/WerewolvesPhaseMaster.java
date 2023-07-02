package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.controller.WerewolfController;
import com.hexszeug.werewolf.game.events.phase.PhaseEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
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
public class WerewolvesPhaseMaster {
    private static final long TIMEOUT_WEREWOLVES_SECONDS = 20;

    private final TaskScheduler taskScheduler;
    private final NarrationService narrationService;

    private final Map<Village, ScheduledFuture<?>> tasks = new HashMap<>();

    @EventListener
    public void handlePhaseEvent(PhaseEvent event) {
        if (event.getPhase() != Phase.WEREWOLVES) return;
        Village village = event.getVillage();
        if (tasks.containsKey(village)) {
            throw new IllegalStateException(
                    "A new werewolves phase was stated before the old one was ended by the WerewolvesPhaseMaster."
            );
        }
        tasks.put(
                village,
                taskScheduler.schedule(
                        () -> werewolfTimerTask(village),
                        Instant.now().plusSeconds(TIMEOUT_WEREWOLVES_SECONDS)
                )
        );
    }

    public void skipTimer(Village village) {
        if (!tasks.containsKey(village)) {
            throw new IllegalStateException("The village has no running werewolf timer.");
        }
        ScheduledFuture<?> task = tasks.get(village);
        if (task.isDone() || task.isCancelled()) {
            throw new IllegalStateException("The werewolf timer task was not properly cleared up.");
        }
        task.cancel(false);
        if (task.isCancelled()) {
            werewolfTimerTask(village);
        }
    }

    private void werewolfTimerTask(Village village) {
        tasks.remove(village);
        findAndSetVictim(village);
        narrationService.continueNarration(village, Phase.WEREWOLVES);
    }

    private void findAndSetVictim(Village village) {
        Map<Player, Integer> votes = new HashMap<>();
        village.getPlayerList().forEach(player -> {
            if (player.getRole() != Role.WEREWOLF) return;
            Player vote = getVote(player, village);
            if (vote != null) {
                votes.compute(vote, (__, count) -> count == null ? 1 : count + 1);
                player.delete(WerewolfController.KEY_VOTE);
            }
        });
        if (votes.isEmpty()) return;
        Set<Player> victims = new HashSet<>();
        AtomicInteger highest = new AtomicInteger(0);
        votes.forEach((player, count) -> {
            if (count > highest.get()) {
                highest.set(count);
                victims.clear();
            }
            if (count == highest.get()) {
                victims.add(player);
            }
        });
        victims
                .stream()
                .skip(new Random().nextInt(victims.size()))
                .findFirst()
                .ifPresent(
                        victim -> village.set(WerewolfController.KEY_WEREWOLF_VICTIM, victim.getPlayerId())
                );
    }

    private Player getVote(Player player, Village village) {
        try {
            return village.getPlayerById(player.get(WerewolfController.KEY_VOTE, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Werewolf vote contains non string value.", ex);
        }
    }
}

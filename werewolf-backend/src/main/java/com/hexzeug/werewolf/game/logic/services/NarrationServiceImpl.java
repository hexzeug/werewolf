package com.hexzeug.werewolf.game.logic.services;

import com.hexzeug.werewolf.game.controller.*;
import com.hexzeug.werewolf.game.events.phase.PhaseEvent;
import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexzeug.werewolf.game.model.player.role.Role;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import com.hexzeug.werewolf.game.model.village.phase.PhaseHistoryElementImpl;
import com.hexzeug.werewolf.game.model.village.teams.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NarrationServiceImpl implements NarrationService {
    private static final Set<Phase> DAY_PHASES = Set.of(Phase.HUNTER, Phase.ACCUSATION, Phase.COURT);

    private final KillingService killingService;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, String> locks = new HashMap<>();

    @Override
    public void continueNarration(Village village, Phase currentPhase) {
        if (village.getCurrentPhase() != currentPhase) return;
        int igtimeOnCall = village.getIGTime();
        // this is a completely weird hack and should probably be changed
        synchronized (locks.computeIfAbsent(village.getVillageId(), String::new)) {
            // the phase might have changed while acquiring the lock
            if (village.getCurrentPhase() != currentPhase || igtimeOnCall != village.getIGTime()) return;

            Player victim = getVictim(village);
            Player poisoned = getPoisoned(village);
            List<Phase> phaseOrder = village.getPhaseOrder();
            int i = village.getPhaseOrderIndex();
            Phase nextPhase = currentPhase;
            do {
                i++;
                if (i >= phaseOrder.size()) i = 0;
                Phase oldNextPhase = nextPhase;
                nextPhase = phaseOrder.get(i);
                if (DAY_PHASES.contains(nextPhase)
                        && !DAY_PHASES.contains(oldNextPhase)
                        && (victim != null || poisoned != null)
                ) {
                    village.incrementIGTime();
                    village.delete(WerewolfController.KEY_WEREWOLF_VICTIM);
                    village.delete(WitchController.KEY_WITCH_POISONED);
                    if (poisoned == null) {
                        killingService.kill(victim, DeathReason.NIGHT);
                    } else if (victim == null) {
                        killingService.kill(poisoned, DeathReason.NIGHT);
                    } else {
                        killingService.kill(Set.of(poisoned, victim), DeathReason.NIGHT);
                    }
                }
                if (meetsRequirements(Phase.GAME_END, village)) {
                    nextPhase = Phase.GAME_END;
                    i = -2;
                    break;
                }
            } while (!meetsRequirements(nextPhase, village));
            village.setPhaseOrderIndex(i);
            village.incrementIGTime();
            village.pushPhaseHistoryElement(new PhaseHistoryElementImpl(nextPhase, Instant.now().toEpochMilli()));
            eventPublisher.publishEvent(new PhaseEvent(village));
        }
    }

    private boolean meetsRequirements(Phase newPhase, Village village) {
        return switch (newPhase) {
            case GAME_START -> false;
            case CUPID -> !hasCouple(village) && roleAlive(Role.CUPID, village);
            case SEER -> roleAlive(Role.SEER, village);
            case WITCH_HEAL, WITCH_POISON -> roleAlive(Role.WITCH, village);
            case HUNTER -> {
                int igtime = village.getIGTime();
                yield village.getPlayerList().stream().anyMatch(player ->
                        player.getRole() == Role.HUNTER && !player.isAlive() && player.getDeathIGTime() == igtime
                );
            }
            case WEREWOLVES, ACCUSATION -> true;
            case COURT -> village.getPlayerList().stream().anyMatch(player -> getAccusation(player) != null);
            case GAME_END -> getWinner(village) != null;
        };
    }

    private Player getVictim(Village village) {
        try {
            return village.getPlayerById(village.get(WerewolfController.KEY_WEREWOLF_VICTIM, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Werewolf victim contains non string.", ex);
        }
    }

    private Player getPoisoned(Village village) {
        try {
            return village.getPlayerById(village.get(WitchController.KEY_WITCH_POISONED, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Witch poisoned contains non string.", ex);
        }
    }

    private String getAccusation(Player player) {
        try {
            return player.get(CourtController.KEY_ACCUSATION, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Accusation contains non string.", ex);
        }
    }

    private boolean roleAlive(Role role, Village village) {
        return village
                .getPlayerList()
                .stream()
                .anyMatch(player -> player.getRole() == role && player.isAlive());
    }

    private boolean hasCouple(Village village) {
        try {
            return village.get(CupidController.KEY_COUPLE_MEMBER_1, String.class) != null;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couple member 1 contains non string.", ex);
        }
    }

    private Team getWinner(Village village) {
        try {
            return village.get(InfoController.KEY_WINNER, Team.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Winner contains non Team enum.", ex);
        }
    }
}

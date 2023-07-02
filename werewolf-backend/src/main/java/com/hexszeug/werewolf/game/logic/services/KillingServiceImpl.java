package com.hexszeug.werewolf.game.logic.services;

import com.hexszeug.werewolf.game.controller.CupidController;
import com.hexszeug.werewolf.game.controller.InfoController;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import com.hexszeug.werewolf.game.model.village.teams.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KillingServiceImpl implements KillingService {
    private final VillageRepository villageRepository;

    @Override
    public void kill(Set<Player> players, DeathReason reason) {
        Set<Village> villages = new HashSet<>();
        Set<Player> lovers = new HashSet<>();
        players.forEach(player -> {
            Village village = villageRepository.getByVillageId(player.getVillageId());
            villages.add(village);
            player.setAlive(false);
            player.setDeathIGTime(village.getIGTime());
            player.setDeathReason(reason);
            Player couple1 = getCouplePlayer1(village);
            if (couple1 == null) return;
            Player couple2 = getCouplePlayer2(village);
            if (player == couple1) {
                lovers.add(couple2);
            }
            if (player == couple2) {
                lovers.add(couple1);
            }
        });
        lovers.forEach(player -> {
            if (player.isAlive()) {
                Village village = villageRepository.getByVillageId(player.getVillageId());
                player.setAlive(false);
                player.setDeathIGTime(village.getIGTime());
                player.setDeathReason(DeathReason.HEARTBREAK);
            }
        });
        villages.forEach(this::detectWinning);
    }

    private void detectWinning(Village village) {
        Set<Team> livingTeams = village.getPlayerList()
                .stream()
                .filter(Player::isAlive)
                .map(Player::getRole)
                .map(Team::teamOf)
                .collect(Collectors.toSet());
        if (livingTeams.isEmpty()) {
            village.set(InfoController.KEY_WINNER, Team.NONE);
        } else if (livingTeams.size() == 1) {
            village.set(InfoController.KEY_WINNER, livingTeams.iterator().next());
        } else if (livingTeams.size() == 2 &&
                village.getPlayerList().stream().filter(Player::isAlive).count() == 2 &&
                hasLivingCoupleTeam(village)
        ) {
            village.set(InfoController.KEY_WINNER, Team.COUPLE);
        }
    }

    private Player getCouplePlayer1(Village village) {
        try {
            return village.getPlayerById(village.get(CupidController.KEY_COUPLE_MEMBER_1, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couple member 1 contains non string.", ex);
        }
    }

    private Player getCouplePlayer2(Village village) {
        try {
            return village.getPlayerById(village.get(CupidController.KEY_COUPLE_MEMBER_2, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couple member 2 contains non string.", ex);
        }
    }

    private boolean hasLivingCoupleTeam(Village village) {
        Player couple1 = getCouplePlayer1(village);
        Player couple2 = getCouplePlayer2(village);
        return couple1 != null && couple1.isAlive() && Team.teamOf(couple1.getRole()) != Team.teamOf(couple2.getRole());
    }
}

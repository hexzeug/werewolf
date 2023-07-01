package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.controller.CupidController;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KillingServiceImpl implements KillingService {
    private final VillageRepository villageRepository;

    @Override
    public void kill(Set<Player> players, DeathReason reason) {
        Set<Player> lovers = new HashSet<>();
        players.forEach(player -> {
            Village village = villageRepository.getByVillageId(player.getVillageId());
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
}

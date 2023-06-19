package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DeathsController {
    @GetMapping("/deaths")
    public Map<String, DeathInfo> handleDeaths(Village village) {
        return village
                .getPlayerList()
                .stream()
                .filter(p -> !p.isAlive())
                .collect(Collectors.toMap(
                        Player::getPlayerId,
                        p -> new DeathInfo(p.getRole(), p.getDeathReason(), p.getDeathIGTime())
                ));
    }

    @GetMapping("/deaths/test")
    public void test(Player player) {
        player.setAlive(false);
        player.setDeathReason(DeathReason.NIGHT);
        player.setDeathIGTime(3);
    }

    @Value
    private static class DeathInfo {
        Role role;
        DeathReason reason;
        int igtime;
    }
}

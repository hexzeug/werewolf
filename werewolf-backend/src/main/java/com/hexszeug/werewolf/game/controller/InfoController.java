package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElement;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class InfoController {
    @GetMapping("/narrator")
    public NarrationInfo handleNarrator(Village village) {
        List<PhaseHistoryElement> phaseHistory = village.getPhaseHistory();
        int size = phaseHistory.size();
        Phase previousPhase = size > 1 ? phaseHistory.get(size - 2).getPhase() : null;
        PhaseHistoryElement currentPhase = phaseHistory.get(size - 1);
        return new NarrationInfo(
                previousPhase,
                currentPhase.getPhase(),
                currentPhase.getTimestamp(),
                village.getIGTime()
        );
    }

    @GetMapping("/me")
    public PrivatePlayerInfo handleMe(Player player) {
        return new PrivatePlayerInfo(player.getPlayerId(), player.getRole());
    }

    @GetMapping("/players")
    public PlayersInfo handlePlayers(Player player, Village village) {
        List<Player> playerList = village.getPlayerList();
        int startIndex = playerList.indexOf(player);
        List<String> playerIdList = village
                .getPlayerList()
                .stream()
                .map(Player::getPlayerId)
                .collect(Collectors.toList());
        // rotates the list so the requesting player's id is at index 0
        Collections.rotate(playerIdList, -startIndex);
        Map<String, PublicPlayerInfo> players = playerList
                .stream()
                .collect(Collectors.toMap(
                        Player::getPlayerId,
                        p -> new PublicPlayerInfo(p.getName())
                ));
        return new PlayersInfo(playerIdList, players);
    }

    @Value
    private static class NarrationInfo {
        Phase previousPhase;
        Phase currentPhase;
        long since;
        int igtime;
    }

    @Value
    private static class PrivatePlayerInfo {
        String id;
        Role role;
    }

    @Value
    private static class PlayersInfo {
        List<String> ids;
        Map<String, PublicPlayerInfo> players;
    }

    @Value
    private static class PublicPlayerInfo {
        String name;
    }
}

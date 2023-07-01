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
    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p><p>
     * <b>Response:</b>
     * <pre>
     * {
     *     previousPhase: {@link Phase}
     *     currentPhase: {@link Phase}
     *     sinceTimestamp: {@code long} (the timestamp since when the current phase is active)
     *     igtime: {@code int} (current in-game time)
     * }
     * </pre>
     * </p>
     */
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

    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p><p>
     * <b>Response:</b>
     * <pre>
     * {
     *     {@code <own player id>}: {
     *         role: {@link Role}
     *     }
     * }
     * </pre>
     * </p>
     */
    @GetMapping("/me")
    public Map<String, PrivatePlayerInfo> handleMe(Player player) {
        return Map.of(player.getPlayerId(), new PrivatePlayerInfo(player.getRole()));
    }

    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p><p>
     * <b>Response:</b>
     * <pre>
     * {
     *     ids: [ (consistent order)
     *         {@link String} (player id)
     *         ...
     *     ]
     *     players: {
     *         {@code <player id>}: {
     *             name: {@link String}
     *         }
     *         ...
     *     }
     * }
     * </pre>
     * </p>
     * */
    @GetMapping("/players")
    public PlayersInfo handlePlayers(Player player, Village village) {
        return new PlayersInfo(
                village
                        .getPlayerList()
                        .stream()
                        .map(Player::getPlayerId)
                        .toList(),
                village
                        .getPlayerList()
                        .stream()
                        .collect(Collectors.toMap(
                                Player::getPlayerId,
                                p -> new PublicPlayerInfo(p.getName())
                        ))
        );
    }

    @Value
    private static class NarrationInfo {
        Phase previousPhase;
        Phase currentPhase;
        long sinceTimestamp;
        int igtime;
    }

    @Value
    private static class PrivatePlayerInfo {
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

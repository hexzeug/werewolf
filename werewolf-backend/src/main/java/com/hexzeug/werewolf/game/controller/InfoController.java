package com.hexzeug.werewolf.game.controller;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.role.Role;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import com.hexzeug.werewolf.game.model.village.phase.PhaseHistoryElement;
import com.hexzeug.werewolf.game.model.village.teams.Team;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class InfoController {
    public static final String KEY_WINNER = "winner";

    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p>
     * <b>Response:</b>
     * <pre>
     * {
     *     previousPhase: {@link Phase}
     *     currentPhase: {@link Phase}
     *     sinceTimestamp: {@code long} (the timestamp since when the current phase is active)
     *     igtime: {@code int} (current in-game time)
     * }
     * </pre>
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
     * </p>
     * <b>Response:</b>
     * <pre>
     * {
     *     {@code <own player id>}: {
     *         role: {@link Role}
     *     }
     * }
     * </pre>
     */
    @GetMapping("/me")
    public Map<String, PrivatePlayerInfo> handleMe(Player player) {
        return Map.of(player.getPlayerId(), new PrivatePlayerInfo(player.getRole()));
    }

    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p>
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

    /**
     * <b>Permissions:</b>
     * <p>
     * none
     * </p>
     * <b>Response:</b>
     * <pre>
     * {@link Team}
     * or
     * {@code null} (if the game is still running)
     * </pre>
     * */
    @GetMapping(value = "/winner", produces = MediaType.APPLICATION_JSON_VALUE)
    public Team handleWinner(Village village) {
        return getWinner(village);
    }

    private Team getWinner(Village village) {
        try {
            return village.get(KEY_WINNER, Team.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Winner contains non Team enum.", ex);
        }
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

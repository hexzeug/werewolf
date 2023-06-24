package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SeerController {
    private static final String KEY_SEER_HISTORY = "seerHistory";

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a seer</li>
     * </ol>
     * <b>Response:</b>
     * <pre>
     * {
     *     {@code <player id>}: {
     *         role: {@link Role}
     *         igtime: {@code int}
     *     }
     *     ...
     * }
     * </pre>
     * @throws ForbiddenException if the player does not fulfill the permissions
     */
    @GetMapping("/seer")
    public Map<String, SeenInfo> handleGet(Player player, Village village) {
        if (player.getRole() != Role.SEER) {
            throw new ForbiddenException("You must be seer.");
        }
        Map<?, ?> seerHistory;
        try {
            seerHistory = player.get(KEY_SEER_HISTORY, Map.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Seer history is not a map.", ex);
        }
        if (seerHistory == null || seerHistory.isEmpty()) {
            return Collections.emptyMap();
        }
        return seerHistory
                .entrySet()
                .stream()
                .map(entry -> {
                    if (!(entry.getKey() instanceof String playerId)) {
                        throw new IllegalStateException("Seer history contains non string key.");
                    }
                    if (!(entry.getValue() instanceof Integer IGTime)) {
                        throw new IllegalStateException("Seer history contains non integer value.");
                    }
                    Player seen = village.getPlayerById(playerId);
                    if (seen == null) {
                        throw new IllegalStateException("Seer history contains non existing player.");
                    }
                    return new AbstractMap.SimpleImmutableEntry<>(
                            playerId,
                            new SeenInfo(seen.getRole(), IGTime)
                    );
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Value
    private static class SeenInfo {
        Role role;
        int igtime;
    }
}

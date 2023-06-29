package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
        return getSeerHistory(player)
                .entrySet()
                .stream()
                .map(entry -> {
                    String playerId = entry.getKey();
                    Player seen = village.getPlayerById(playerId);
                    if (seen == null) {
                        throw new IllegalStateException("Seer history contains non existing player.");
                    }
                    return (Map.Entry<String, SeenInfo>) new AbstractMap.SimpleImmutableEntry<>(
                            playerId,
                            new SeenInfo(seen.getRole(), entry.getValue())
                    );
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a seer</li>
     *     <li>seer phase is currently active</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@link String} (player id)
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 201 CREATED}
     * </p>
     * <b>Effects:</b>
     * <ul>
     *     <li>pushes player to list of seen people (doesn't check if already seen)</li>
     *     <li>continues narration</li>
     * </ul>
     * @throws ForbiddenException if the permissions are not fulfilled
     * @throws BadRequestException if passed string is not a valid player id of the village
     */
    @PostMapping(value = "/seer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void handlePost(@RequestBody TextNode playerIdTextNode, Player player, Village village) {
        String playerId = playerIdTextNode.asText();
        System.out.println(playerId);
        if (player.getRole() != Role.SEER) {
            throw new ForbiddenException("You must be seer.");
        }
        if (village.getCurrentPhase() != Phase.SEER) {
            throw new ForbiddenException("It must be seer phase.");
        }
        if (village.getPlayerById(playerId) == null) {
            throw new BadRequestException("Player %s is not in the village.".formatted(playerId));
        }
        Map<String, Integer> seerHistory = new HashMap<>(getSeerHistory(player));
        seerHistory.put(playerId, village.getIGTime());
        player.set(KEY_SEER_HISTORY, seerHistory);
        //TODO continue narration
    }

    private Map<String, Integer> getSeerHistory(Player player) {
        Map<?, ?> uncheckedSeerHistory;
        try {
            uncheckedSeerHistory = player.get(KEY_SEER_HISTORY, Map.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Seer history is not a map.", ex);
        }
        if (uncheckedSeerHistory == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> seerHistory = new HashMap<>();
        uncheckedSeerHistory.forEach((k, v) -> {
            if (!(k instanceof String key)) {
                throw new IllegalStateException("Seer history contains non string key.");
            }
            if (!(v instanceof Integer value)) {
                throw new IllegalStateException("Seer history contains non integer value.");
            }
            seerHistory.put(key, value);
        });
        return seerHistory;
    }

    @Value
    private static class SeenInfo {
        Role role;
        int igtime;
    }

    @Value
    private static class PlayerToBeSeenInfo {
        @JsonValue
        String playerId;
    }
}

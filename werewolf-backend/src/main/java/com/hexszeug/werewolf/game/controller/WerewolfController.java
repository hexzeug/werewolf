package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/werewolves")
@RequiredArgsConstructor
public class WerewolfController {
    private static final String KEY_VOTE = "werewolfVote";

    private final ApplicationEventPublisher eventPublisher;

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a werewolf</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {@link String} (player id)
     *     ...
     * ]
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping
    public List<String> handleGetWerewolves(Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF) {
            throw new ForbiddenException("You must be a werewolf.");
        }
        return village.getPlayerList()
                .stream()
                .filter(p -> p.getRole() == Role.WEREWOLF)
                .map(Player::getPlayerId)
                .toList();
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a werewolf</li>
     *     <li>werewolves phase is current phase</li>
     * </ol>
     * <b>Response:</b>
     * <pre>
     * {
     *     {@code <player id>}: [ (player id of target)
     *         {@link String} (player id of werewolf)
     *         ...
     *     ]
     *     ...
     * }
     * </pre>
     */
    @GetMapping("/votes")
    public Map<String, List<String>> handleGetVotes(Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF) {
            throw new ForbiddenException("You must be a werewolf.");
        }
        if (village.getCurrentPhase() != Phase.WEREWOLVES) {
            throw new ForbiddenException("The werewolves phase must be the current phase.");
        }
        Map<String, List<String>> votes = new HashMap<>();
        getVotes(village).forEach(
                (vote, list) -> votes.put(
                        vote.getPlayerId(),
                        list.stream().map(Player::getPlayerId).toList()
                )
        );
        return votes;
    }

    @PutMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePutVote(@RequestBody TextNode voteTextNode, Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF) {
            throw new ForbiddenException("You must be a werewolf.");
        }
        if (village.getCurrentPhase() != Phase.WEREWOLVES) {
            throw new ForbiddenException("The werewolves phase must be the current phase.");
        }
        String vote = voteTextNode.asText();
        if (village.getPlayerById(vote) == null) {
            throw new BadRequestException("Player %s is not in the village.".formatted(vote));
        }
        player.set(KEY_VOTE, vote);
        eventPublisher.publishEvent(new WerewolfVoteEvent(
                new VoteInfo(player.getPlayerId(), vote),
                village.getVillageId()
        ));
        if (getVotes(village).size() == 1 &&
                village.getPlayerList()
                        .stream()
                        .noneMatch(p ->
                                p.getRole() == Role.WEREWOLF &&
                                        getVote(p, village) == null
                        )
        ) {
            //TODO continue narration
        }
    }

    private Map<Player, List<Player>> getVotes(Village village) {
        Map<Player, List<Player>> votes = new HashMap<>();
        village.getPlayerList().stream()
                .filter(player -> player.getRole() == Role.WEREWOLF)
                .forEach(player -> {
                    Player vote = getVote(player, village);
                    if (vote != null) {
                        votes
                                .computeIfAbsent(vote, key -> new ArrayList<>())
                                .add(player);
                    }
                });
        return votes;
    }

    private Player getVote(Player player, Village village) {
        try {
            return village.getPlayerById(player.get(KEY_VOTE, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Werewolf vote contains non string.", ex);
        }
    }

    @Value
    public static class WerewolfVoteEvent implements ServerSentEvent<VoteInfo> {
        VoteInfo payload;
        String villageId;

        @Override
        public boolean isTarget(Player player) {
            return player.getRole() == Role.WEREWOLF;
        }
    }

    @Value
    private static class VoteInfo {
        String voter;
        String vote;
    }
}

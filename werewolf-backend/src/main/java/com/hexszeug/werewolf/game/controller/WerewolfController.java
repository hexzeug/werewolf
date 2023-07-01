package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.events.werewolf.WerewolfVoteEvent;
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
    public static final String KEY_WEREWOLF_VICTIM = "werewolfVictim";

    private final ApplicationEventPublisher eventPublisher;

    /**
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
     * @throws ForbiddenException if permissions are not fulfilled
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

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a living werewolf</li>
     *     <li>werewolves phase is current phase</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@link String} (player id)
     * </pre>
     * <b>Response</b>
     * <p>
     *     {@code 201 CREATED}
     * </p>
     * <b>Effects:</b>
     * <ul>
     *     <li>stores vote</li>
     *     <li>publishes {@link WerewolfVoteEvent} to werewolf clients</li>
     *     <li><i>if all werewolves vote the same person:</i> continues narration</li>
     * </ul>
     * @throws ForbiddenException if the permissions are not fulfilled
     * @throws BadRequestException if passed string is not a valid player id of the village
     */
    @PutMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePutVote(@RequestBody TextNode voteTextNode, Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF || !player.isAlive()) {
            throw new ForbiddenException("You must be a living werewolf.");
        }
        if (village.getCurrentPhase() != Phase.WEREWOLVES) {
            throw new ForbiddenException("The werewolves phase must be the current phase.");
        }
        String playerId = voteTextNode.asText();
        Player vote = village.getPlayerById(playerId);
        if (vote == null) {
            throw new BadRequestException("Player %s is not in the village.".formatted(playerId));
        }
        if (!vote.isAlive()) {
            throw new BadRequestException("Player %s is already dead.".formatted(playerId));
        }
        player.set(KEY_VOTE, playerId);
        eventPublisher.publishEvent(new WerewolfVoteEvent(
                new VoteInfo(player.getPlayerId(), playerId),
                village.getVillageId()
        ));
        //noinspection StatementWithEmptyBody
        if (getVotes(village).size() == 1 &&
                village.getPlayerList()
                        .stream()
                        .noneMatch(p ->
                                p.getRole() == Role.WEREWOLF &&
                                        getVote(p, village) == null
                        )
        ) {
            //TODO continue narration (and cancel timeout + set victim)
        }
    }

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a werewolf</li>
     *     <li>werewolves phase is current phase</li>
     * </ol>
     * <b>Response</b>
     * <p>
     *     {@code 204 NO CONTENT}
     * </p>
     * <b>Effects:</b>
     * <ul>
     *     <li>deletes vote</li>
     *     <li>publishes {@link WerewolfVoteEvent} to werewolf clients</li>
     * </ul>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @DeleteMapping("/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDeleteVote(Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF) {
            throw new ForbiddenException("You must be a werewolf.");
        }
        if (village.getCurrentPhase() != Phase.WEREWOLVES) {
            throw new ForbiddenException("The werewolves phase must be the current phase.");
        }
        player.delete(KEY_VOTE);
        eventPublisher.publishEvent(new WerewolfVoteEvent(
                new VoteInfo(player.getPlayerId(), null),
                village.getVillageId()
        ));
    }

    /**
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a werewolf</li>
     *     <li>player is a witch</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {@link String} (player id)
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping("/victim")
    public VictimInfo handleGetVictim(Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF && player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must either be a werewolf or a witch.");
        }
        try {
            return new VictimInfo(village.get(KEY_WEREWOLF_VICTIM, String.class));
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Werewolf victim contains non string.", ex);
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
    public static class VoteInfo {
        String voter;
        String vote;
    }

    @Value
    private static class VictimInfo {
        @JsonValue
        String playerId;
    }
}

package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.events.court.AccusationEvent;
import com.hexszeug.werewolf.game.events.court.CourtVoteEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourtController {
    public static final String KEY_ACCUSATION = "accusation";
    public static final String KEY_VOTE = "vote";

    private final ApplicationEventPublisher eventPublisher;

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>current phase is accusation</li>
     *     <li>current phase is court</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {
     *         accuser: {@link String} (player id)
     *         accused: {@link String} (player id)
     *     }
     *     ...
     * ]
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping("/accusations")
    public List<AccusationInfo> handleGetAccusations(Village village) {
        if (village.getCurrentPhase() != Phase.ACCUSATION && village.getCurrentPhase() != Phase.COURT) {
            throw new ForbiddenException("The phase must either be accusation or court.");
        }
        return village.getPlayerList()
                .stream()
                .map(player -> {
                    String accusation = getAccusation(player);
                    if (accusation == null) return null;
                    return new AccusationInfo(player.getPlayerId(), accusation);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>current phase is accusation</li>
     *     <li>player is alive</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@link String} (player id)
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 204 NO CONTENT}
     * </p>
     * <b>Effects:</b>
     * <ol>
     *     <li>set own accusation (if target player is already accused exception is thrown)</li>
     *     <li>publishes {@link AccusationEvent}</li>
     * </ol>
     * @throws ForbiddenException if the permissions are not fulfilled
     * @throws BadRequestException if the passed player does not exist of is dead or is already accused
     */
    @PutMapping(value = "/accusation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePutAccusation(@RequestBody TextNode playerIdTextNode, Player player, Village village) {
        if (village.getCurrentPhase() != Phase.ACCUSATION) {
            throw new ForbiddenException("The phase must be accusation.");
        }
        if (!player.isAlive()) {
            throw new ForbiddenException("You must be alive.");
        }
        String playerId = playerIdTextNode.asText();
        Player accusation = village.getPlayerById(playerId);
        if (accusation == null) {
            throw new BadRequestException("Player %s does not exist in this village.".formatted(playerId));
        }
        if (!accusation.isAlive()) {
            throw new BadRequestException("Player %s is already dead.".formatted(playerId));
        }
        if (village.getPlayerList().stream().anyMatch(p -> playerId.equals(getAccusation(p)))) {
            throw new BadRequestException("Player %s is already accused.".formatted(playerId));
        }
        player.set(KEY_ACCUSATION, playerId);
        eventPublisher.publishEvent(new AccusationEvent(
                new AccusationInfo(player.getPlayerId(), playerId),
                village.getVillageId()
        ));
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>current phase is accusation</li>
     *     <li>player is alive</li>
     * </ol>
     * <b>Response:</b>
     * <p>
     *     {@code 204 NO CONTENT}
     * </p>
     * <b>Effects:</b>
     * <ol>
     *     <li>deletes own accusation (if not accusing an exception is thrown)</li>
     *     <li>publishes {@link AccusationEvent}</li>
     * </ol>
     * @throws ForbiddenException if the permissions are not fulfilled
     * @throws BadRequestException if the requesting player does not accuse anyone
     */
    @DeleteMapping("/accusation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDeleteAccusation(Player player, Village village) {
        if (village.getCurrentPhase() != Phase.ACCUSATION) {
            throw new ForbiddenException("The phase must be accusation.");
        }
        if (!player.isAlive()) {
            throw new ForbiddenException("You must be alive.");
        }
        if (getAccusation(player) == null) {
            throw new BadRequestException("You are not accusing anybody.");
        }
        player.delete(KEY_ACCUSATION);
        eventPublisher.publishEvent(new AccusationEvent(
                new AccusationInfo(player.getPlayerId(), null),
                village.getVillageId()
        ));
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>current phase is court</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {
     *         voter: {@link String} (player id)
     *         vote: {@link String} (player id)
     *     }
     *     ...
     * ]
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping("/votes")
    public List<VoteInfo> handleGetVotes(Village village) {
        if (village.getCurrentPhase() != Phase.COURT) {
            throw new ForbiddenException("The phase must be court.");
        }
        return village.getPlayerList()
                .stream()
                .map(p -> {
                    String vote = getVote(p);
                    if (vote == null) return null;
                    return new VoteInfo(p.getPlayerId(), vote);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>current phase is court</li>
     *     <li>player is alive</li>
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
     * <ol>
     *     <li>set own vote (if target player is not accused exception is thrown)</li>
     *     <li>publishes {@link CourtVoteEvent}</li>
     * </ol>
     * @throws ForbiddenException if the permissions are not fulfilled
     * @throws BadRequestException if the requesting player has already voted,
     *                             or the passed player does not exist or is not accused
     */
    @PostMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostVote(@RequestBody TextNode playerIdTextNode, Player player, Village village) {
        if (village.getCurrentPhase() != Phase.COURT) {
            throw new ForbiddenException("The phase must be court.");
        }
        if (!player.isAlive()) {
            throw new ForbiddenException("You must be alive.");
        }
        if (getVote(player) != null) {
            throw new BadRequestException("You already have voted.");
        }
        String playerId = playerIdTextNode.asText();
        if (village.getPlayerById(playerId) == null) {
            throw new BadRequestException("Player %s does not exist in this village.".formatted(playerId));
        }
        if (village.getPlayerList().stream().noneMatch(p -> playerId.equals(getAccusation(p)))) {
            throw new BadRequestException("Player %s is not accused.".formatted(playerId));
        }
        player.set(KEY_VOTE, playerId);
        eventPublisher.publishEvent(new CourtVoteEvent(
                new VoteInfo(player.getPlayerId(), playerId),
                village.getVillageId()
        ));
    }

    private String getAccusation(Player player) {
        try {
            return player.get(KEY_ACCUSATION, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Accusation contains non string.", ex);
        }
    }

    private String getVote(Player player) {
        try {
            return player.get(KEY_VOTE, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Vote contains non string.", ex);
        }
    }

    @Value
    public static class AccusationInfo {
        String accuser;
        String accused;
    }

    @Value
    public static class VoteInfo {
        String voter;
        String vote;
    }
}

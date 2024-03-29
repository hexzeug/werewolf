package com.hexzeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.hexzeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexzeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexzeug.werewolf.game.logic.services.NarrationService;
import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.role.Role;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/witch")
@RequiredArgsConstructor
public class WitchController {
    private static final String KEY_DID_HEAL = "witchDidHeal";
    private static final String KEY_DID_POISON = "witchDidPoison";
    public static final String KEY_WITCH_POISONED = "witchPoisoned";

    private final NarrationService narrationService;

    /**
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a witch</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {@code boolean} (true = heal potion left, false = potion already used)
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping(value = "/heal", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean handleGetHeal(Player player) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        // note the boolean is inverted now representing the existence of a heeling potion
        return !didHeal(player);
    }

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a witch</li>
     *     <li>current phase is witch heal</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@code boolean}
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 204 NO CONTENT}
     * </p>
     * <b>Effects:</b>
     * <ol>
     *     <li>delete werewolf victim if body was {@code true} (request fails if heal potion already used)</li>
     *     <li>continue narration</li>
     * </ol>
     * @throws ForbiddenException if permissions are not fulfilled
     * @throws BadRequestException if body is {@code true} although heal potion was already used
     */
    @PostMapping(value = "/heal", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePostHeal(@RequestBody BooleanNode booleanNode, Player player, Village village) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        if (village.getCurrentPhase() != Phase.WITCH_HEAL) {
            throw new ForbiddenException("The phase must be witch heal.");
        }
        if (booleanNode.asBoolean()) {
            if (didHeal(player)) {
                throw new BadRequestException("The heal potion was already used.");
            }
            village.delete(WerewolfController.KEY_WEREWOLF_VICTIM);
            player.set(KEY_DID_HEAL, true);
        }
        narrationService.continueNarration(village, Phase.WITCH_HEAL);
    }

    /**
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a witch</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {@code boolean} (true = poison potion left, false = potion already used)
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping(value = "/poison", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean handleGetPoison(Player player) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        // note the boolean is inverted now representing the existence of a poison potion
        return !didPoison(player);
    }

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a witch</li>
     *     <li>current phase is witch heal</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@link String} or {@code null} (player id)
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 204 NO CONTENT}
     * </p>
     * <b>Effects:</b>
     * <ol>
     *     <li>set witch poisoned to passed player id (or do nothing if {@code null})</li>
     *     <li>continue narration</li>
     * </ol>
     * @throws ForbiddenException if permissions are not fulfilled
     * @throws BadRequestException if the body is not null and poison potion was already used
     *                             or if the player does not exist
     *                             or if the player is already dead
     *                             or if the body contains neither a {@link String} nor {@code null}
     */
    @PostMapping(value = "/poison", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePostPoison(@RequestBody ValueNode playerIdNode, Player player, Village village) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        if (village.getCurrentPhase() != Phase.WITCH_POISON) {
            throw new ForbiddenException("The phase must be witch poison.");
        }
        if (!playerIdNode.isNull() && !playerIdNode.isTextual()) {
            throw new BadRequestException("The body must either contain a string or null.");
        }
        if (playerIdNode.isTextual()) {
            String playerId = playerIdNode.asText();
            Player poisoned = village.getPlayerById(playerId);
            if (poisoned == null) {
                throw new BadRequestException("Player %s does not exist in the village.".formatted(playerId));
            }
            if (didPoison(player)) {
                throw new BadRequestException("The poison potion was already used.");
            }
            if (!poisoned.isAlive()) {
                throw new BadRequestException("The targeted player %s is already dead.".formatted(playerId));
            }
            if (poisoned.getPlayerId().equals(getVictim(village))) {
                throw new BadRequestException("The targeted player %s is the werewolf victim.".formatted(playerId));
            }
            village.set(KEY_WITCH_POISONED, playerId);
            player.set(KEY_DID_POISON, true);
        }
        narrationService.continueNarration(village, Phase.WITCH_POISON);
    }

    private boolean didHeal(Player player) {
        try {
            Boolean heal = player.get(KEY_DID_HEAL, Boolean.class);
            return heal != null && heal;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Witch did heal contains non boolean.", ex);
        }
    }

    private boolean didPoison(Player player) {
        try {
            Boolean heal = player.get(KEY_DID_POISON, Boolean.class);
            return heal != null && heal;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Witch did poison contains non boolean.", ex);
        }
    }

    private String getVictim(Village village) {
        try {
            return village.get(WerewolfController.KEY_WEREWOLF_VICTIM, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Werewolf victim contains non string.", ex);
        }
    }
}

package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/witch")
public class WitchController {
    private static final String KEY_HEALED = "witchHealed";

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a witch</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {@code boolean}
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping(value = "/heal", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean handleGetHeal(Player player) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        // note the boolean is inverted now representing the existence of a heeling potion
        return !getHealed(player);
    }

    /**
     * @apiNote
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
            if (getHealed(player)) {
                throw new BadRequestException("The heal potion was already used.");
            }
            village.delete(WerewolfController.KEY_WEREWOLF_VICTIM);
            player.set(KEY_HEALED, true);
        }
        //TODO continue narration
    }

    private boolean getHealed(Player player) {
        try {
            Boolean heal = player.get(KEY_HEALED, Boolean.class);
            return heal != null && heal;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Witch heal contains non boolean.", ex);
        }
    }
}

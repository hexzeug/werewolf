package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.logic.KillingService;
import com.hexszeug.werewolf.game.logic.NarrationService;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HunterController {
    private final KillingService killingService;
    private final NarrationService narrationService;

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is a hunter</li>
     *     <li>current phase is hunter phase</li>
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
     *     <li>kill the passed player</li>
     *     <li>continue narration</li>
     * </ol>
     * @throws ForbiddenException if permissions are not fulfilled
     * @throws BadRequestException if the player does not exist or is already dead
     */
    @PostMapping(value = "/hunter", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostHunter(@RequestBody TextNode playerIdTextNode, Player player, Village village) {
        if (player.getRole() != Role.HUNTER) {
            throw new ForbiddenException("You must be a hunter.");
        }
        if (village.getCurrentPhase() != Phase.HUNTER) {
            throw new ForbiddenException("The phase must be the hunter phase.");
        }
        String playerId = playerIdTextNode.asText();
        Player target = village.getPlayerById(playerId);
        if (target == null) {
            throw new BadRequestException("Player %s does not exist in the village.".formatted(playerId));
        }
        if (!target.isAlive()) {
            throw new BadRequestException("Player %s is already dead.".formatted(playerId));
        }
        killingService.kill(target, DeathReason.SHOT);
        narrationService.continueNarration(village, Phase.HUNTER);
    }
}

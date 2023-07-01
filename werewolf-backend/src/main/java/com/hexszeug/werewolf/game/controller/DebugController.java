package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.logic.KillingService;
import com.hexszeug.werewolf.game.logic.NarrationService;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElementImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

//TODO delete
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    private final KillingService killingService;
    private final NarrationService narrationService;

    @PutMapping(value = "/phase", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePutPhase(@RequestBody TextNode phaseName, Village village) {
        try {
            Phase phase = Phase.valueOf(phaseName.asText().toUpperCase(Locale.ROOT));
            village.pushPhaseHistoryElement(new PhaseHistoryElementImpl(phase, System.currentTimeMillis()));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Bad phase name %s.".formatted(phaseName.asText()));
        }
    }

    @PostMapping("/increment_igtime")
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostIncrementIGTime(Village village) {
        village.incrementIGTime();
    }

    @PutMapping(value = "/werewolf/victim", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePutWerewolfVictim(@RequestBody TextNode playerId, Village village) {
        if (village.getPlayerById(playerId.asText()) == null) {
            throw new BadRequestException("Bad player id %s.".formatted(playerId.asText()));
        }
        village.set(WerewolfController.KEY_WEREWOLF_VICTIM, playerId.asText());
    }

    @PostMapping("/kill")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleKill(Player player, Village village) {
        killingService.kill(player, DeathReason.NIGHT);
    }

    @PostMapping("/end_phase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleEndPhase(Village village) {
        narrationService.continueNarration(village, village.getCurrentPhase());
    }
}

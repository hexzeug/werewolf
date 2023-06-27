package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElementImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

//TODO delete
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    @PutMapping("/phase/{phaseName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePutPhase(@PathVariable String phaseName, Village village) {
        try {
            Phase phase = Phase.valueOf(phaseName.toUpperCase(Locale.ROOT));
            village.pushPhaseHistoryElement(new PhaseHistoryElementImpl(phase, System.currentTimeMillis()));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @PostMapping("/increment_igtime")
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostIncrementIGTime(Village village) {
        village.incrementIGTime();
    }
}

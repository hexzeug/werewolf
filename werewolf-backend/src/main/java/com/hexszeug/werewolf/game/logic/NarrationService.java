package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;

/**
 * Service, which is used to lead a werewolf game through its phases and activate them if they are needed.
 */
public interface NarrationService {
    /**
     * Used to continue narration to the next phase.
     * You have to pass the current phase you want to end,
     * so you don't end a different phase by mistake.
     * @param village the village to perform the phase change on
     * @param currentPhase used to provide thread-safety,
     *                     should be hardcoded rather than using {@code village.getCurrentPhase()}
     */
    void continueNarration(Village village, Phase currentPhase);
}

package com.hexszeug.werewolf.game.logic.phasemasters;

import com.hexszeug.werewolf.game.model.village.Village;

/**
 * Phase master of the werewolf phase.
 * Provides the ability to skip the phase timeout.
 */
public interface WerewolvesPhaseMaster {
    /**
     * Skips the timeout to the end and immediately runs the phase end procedure.
     * @param village the village to skip the timeout on
     */
    void skipTimer(Village village);
}

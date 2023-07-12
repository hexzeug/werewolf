package com.hexzeug.werewolf.game.logic.phasemasters;

import com.hexzeug.werewolf.game.model.village.Village;

/**
 * Phase master of the court phase.
 * Provides the ability to skip the phase timeout.
 */
public interface CourtPhaseMaster {
    /**
     * Skips the timeout to the end and immediately runs the phase end procedure.
     * @param village the village to skip the timeout on
     */
    void skipCourtTimer(Village village);
}

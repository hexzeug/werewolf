package com.hexzeug.werewolf.game.model.village.phase;

import com.hexzeug.werewolf.game.model.village.Village;

/**
 * The PhaseHistoryElement interface represents an element in the phase history of a
 * {@link Village Village}.
 * It provides methods to retrieve the phase and timestamp associated with the element.
 */
public interface PhaseHistoryElement {

    /**
     * Returns the phase associated with this history element.
     *
     * @return the Phase object representing the phase
     */
    Phase getPhase();

    /**
     * Returns the timestamp of when the phase occurred.
     *
     * @return the timestamp of the phase
     */
    long getTimestamp();
}

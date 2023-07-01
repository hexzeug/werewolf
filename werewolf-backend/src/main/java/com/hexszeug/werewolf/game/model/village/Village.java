package com.hexszeug.werewolf.game.model.village;

import com.hexszeug.werewolf.game.model.customprops.CustomProperties;
import com.hexszeug.werewolf.game.model.player.PlayerRepository;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElement;

import java.util.List;

/**
 * The Village interface represents a game village.
 * It provides access to multiple game logic related fields,
 * to any custom properties through the {@link CustomProperties} interface
 * and to the players in the village through the {@link PlayerRepository} interface.
 */
public interface Village extends CustomProperties, PlayerRepository {

    /**
     * Returns the ID of the village.
     *
     * @return the village ID
     */
    String getVillageId();

    /**
     * Returns the order of phases in the village for one night-day cycle.
     *
     * @return a list of the Phase enums values representing the phase order
     */
    List<Phase> getPhaseOrder();

    /**
     * Returns the index of the current phase in the phase order.
     * An index of {@code -1} means the current phase is not in the phase order.
     *
     * @return the index of the current phase
     */
    int getPhaseOrderIndex();

    /**
     * Sets the index of the current phase in the phase order.
     *
     * @param phaseOrderIndex the index of the current phase to be set
     */
    void setPhaseOrderIndex(int phaseOrderIndex);

    /**
     * Returns the in-game time of the village as an integer.
     * Mostly it is incremented with each new phase
     * but there are other cases like when revealing player's deaths at sunrise
     * when it is incremented without the phase changing.
     *
     * @return the in-game time of the village
     */
    int getIGTime();

    /**
     * Increments the in-game time of the village.
     */
    void incrementIGTime();

    /**
     * Returns the history of phases in the village.
     *
     * @return a list of {@link PhaseHistoryElement} objects representing the phase history
     */
    List<PhaseHistoryElement> getPhaseHistory();

    /**
     * Can be used to access the current phase.
     * Must return the phase property of the last {@code PhaseHistoryElement} returned by {@link #getPhaseHistory()}.
     * @return the current phase
     */
    Phase getCurrentPhase();

    /**
     * Adds a new phase history element to the village's phase history.
     *
     * @param phaseHistoryElement the phase history element to be added
     */
    void pushPhaseHistoryElement(PhaseHistoryElement phaseHistoryElement);
}


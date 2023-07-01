package com.hexszeug.werewolf.game.model.player.deathreason;

import com.hexszeug.werewolf.utils.SerializeToLowercaseEnum;

/**
 * Enumeration representing the different reasons for a player's death in a werewolf game.
 */
public enum DeathReason implements SerializeToLowercaseEnum {
    /**
     * The player was executed by a vote during the accusation phase.
     */
    EXECUTED,

    /**
     * The player died of a broken heart due to being separated from their lover.
     */
    HEARTBREAK,

    /**
     * The player died during the night phase.
     */
    NIGHT,

    /**
     * The player was shot by the Hunter upon their death.
     */
    SHOT,
}

package com.hexszeug.werewolf.game.model.player.deathreason;

/**
 * Represents the reason for a player's death.
 */
public enum DeathReason {
    /**
     * Player was declared guilty by the court and executed.
     */
    EXECUTED,
    /**
     * Player died because they were in the couple and the other player in the couple died.
     */
    HEARTBREAK,
    /**
     * Player died during night thus the exact death reason is hidden.
     */
    NIGHT,
    /**
     * Player was shot by a hunter.
     */
    SHOT,
}

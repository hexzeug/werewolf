package com.hexszeug.werewolf.game.model.village.phase;

/**
 * Enumeration representing the different phases in a werewolf game.
 */
public enum Phase {
    /**
     * The initial phase when the game starts.
     */
    GAME_START,

    /**
     * The phase where the Cupid selects two players to fall in love.
     */
    CUPID,

    /**
     * The phase where the Seer identifies the role of a player.
     */
    SEER,

    /**
     * The phase where the werewolves choose a player to eliminate.
     */
    WEREWOLVES,

    /**
     * The phase where the Witch can choose to heal a player.
     */
    WITCH_HEAL,

    /**
     * The phase where the Witch can choose to poison a player.
     */
    WITCH_POISON,

    /**
     * The phase where the Hunter can choose to shoot a player upon death.
     */
    HUNTER,

    /**
     * The phase where players accuse each other and vote for elimination.
     */
    ACCUSATION,

    /**
     * The phase where the court decides the fate of an accused player.
     */
    COURT,
}

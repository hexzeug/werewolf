package com.hexszeug.werewolf.game.model.player.role;

/**
 * Enumeration representing the different roles a player can have in a werewolf game.
 */
public enum Role {
    /**
     * The role of the Cupid, who can select two players to fall in love.
     */
    CUPID,

    /**
     * The role of the Hunter, who can shoot a player upon death.
     */
    HUNTER,

    /**
     * The role of the Seer, who can identify the role of a player.
     */
    SEER,

    /**
     * The role of a regular Villager, with no special abilities.
     */
    VILLAGER,

    /**
     * The role of a Werewolf, who can eliminate players during the game.
     */
    WEREWOLF,

    /**
     * The role of the Witch, who can heal or poison players.
     */
    WITCH,
}

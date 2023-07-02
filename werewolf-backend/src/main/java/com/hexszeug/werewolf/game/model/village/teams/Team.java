package com.hexszeug.werewolf.game.model.village.teams;

import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.utils.SerializeToLowercaseEnum;

/**
 * Represents the different teams in a werewolf game.
 */
public enum Team implements SerializeToLowercaseEnum {
    /**
     * The team of werewolves.
     */
    WEREWOLVES,

    /**
     * The team of villagers.
     */
    VILLAGERS,

    /**
     * The team of the couple.
     */
    COUPLE,
    /**
     * No team at all. Used for draws (all players dead).
     */
    NONE;

    /**
     * Returns the team the role normally has. Won't encounter for couples.
     * @param role the role
     * @return the team the role is associated with
     */
    public static Team teamOf(Role role) {
        return switch (role) {
            case CUPID, HUNTER, SEER, VILLAGER, WITCH -> VILLAGERS;
            case WEREWOLF -> WEREWOLVES;
        };
    }
}

package com.hexszeug.werewolf.game.model.player;

import com.hexszeug.werewolf.game.model.customprops.CustomProperties;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.player.role.Role;

/**
 * The {@code Player} interface represents a player in the werewolf game model.
 * It extends the {@code CustomProperties} interface to enable the storage of custom key-value pairs on the player.
 */
public interface Player extends CustomProperties {

    /**
     * Returns the player ID.
     *
     * @return the player ID as a string.
     */
    String getPlayerId();

    /**
     * Returns the village ID of the player.
     *
     * @return the village ID as a string.
     */
    String getVillageId();

    /**
     * Returns the name of the player.
     *
     * @return the player name as a string.
     */
    String getName();

    /**
     * Returns the role of the player.
     *
     * @return the role of the player.
     */
    Role getRole();

    /**
     * Checks if the player is alive.
     *
     * @return {@code true} if the player is alive, {@code false} otherwise.
     */
    boolean isAlive();

    /**
     * Sets the alive status of the player.
     *
     * @param alive {@code true} if the player is alive, {@code false} otherwise.
     */
    void setAlive(boolean alive);

    /**
     * Returns the reason for the player's death.
     *
     * @return the reason for the player's death.
     */
    DeathReason getDeathReason();

    /**
     * Sets the reason for the player's death.
     *
     * @param deathReason the reason for the player's death.
     */
    void setDeathReason(DeathReason deathReason);

    /**
     * Returns the in-game time of the player's death.
     *
     * @return the in-game time of the player's death.
     */
    int getDeathIGTime();

    /**
     * Sets the in-game time of the player's death.
     *
     * @param deathIGTime the in-game time of the player's death.
     */
    void setDeathIGTime(int deathIGTime);
}

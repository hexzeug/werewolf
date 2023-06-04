package com.hexszeug.werewolf.game.model.player;

import java.util.List;

/**
 * The {@code PlayerRepository} interface represents a repository for managing players.
 */
public interface PlayerRepository {

    /**
     * Returns the player with the specified player ID.
     *
     * @param playerId the ID of the player to retrieve.
     * @return the player with the specified player ID, or {@code null} if not found.
     */
    Player getByPlayerId(String playerId);

    /**
     * Returns a list of all players in the repository.
     * Implementations of this method must guarantee to maintain the order of the players.
     *
     * @return a list containing all players in the repository.
     */
    List<Player> getPlayerList();
}


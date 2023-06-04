package com.hexszeug.werewolf.game.model.player;

import java.util.List;

public interface PlayerRepository {
    Player getByPlayerId(String playerId);
    /**
     * Get all players in the repository in a list.
     * Implementations must guarantee to keep the player's order.
     * @return list of all players in the repository
     */
    List<Player> getPlayerList();
}

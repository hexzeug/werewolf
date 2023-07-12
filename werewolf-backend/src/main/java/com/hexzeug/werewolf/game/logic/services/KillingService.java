package com.hexzeug.werewolf.game.logic.services;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.deathreason.DeathReason;

import java.util.Set;

/**
 * Can be used to kill players essentially just setting their internal state.
 * It also handles players in love.
 */
public interface KillingService {
    /**
     * Kills the passed players in a random order specifying the passed reason as the source.
     * After killing all passed players it is checked if the players had lovers who then would be killed as well,
     * but obviously specifying {@link DeathReason#HEARTBREAK heartbreak} as the reason.
     * @param players a set of the players to kill
     * @param reason the reason why the players have to die
     */
    void kill(Set<Player> players, DeathReason reason);

    /**
     * Kills the passed player and their lover if they had one,
     * specifying the passed reason for the passed player
     * and {@link DeathReason#HEARTBREAK hearbreak} for the lover.
     * @param player the player to kill
     * @param reason the reason why the player has to die
     */
    default void kill(Player player, DeathReason reason) {
        kill(Set.of(player), reason);
    }
}

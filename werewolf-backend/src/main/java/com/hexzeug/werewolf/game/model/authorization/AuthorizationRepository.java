package com.hexzeug.werewolf.game.model.authorization;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.PlayerRepository;

/**
 * The {@code AuthorizationRepository} interface provides methods to retrieve player information
 * based on authentication tokens.
 * <p>
 * Implementations do not need to store the mapping by themselves. They only need to provide access to it.
 * However, it is recommended to use a {@link PlayerRepository PlayerRepository}
 * for getting {@code Player} references.
 * </p>
 * @see Player
 */
public interface AuthorizationRepository {
    /**
     * Retrieves a player based on the provided authentication token.
     *
     * @param authToken The authentication token associated with the player.
     * @return The Player object corresponding to the given authentication token,
     *         or null if no player is found with the given authentication token.
     */
    Player getPlayerByAuthToken(String authToken);
}

package com.hexszeug.werewolf.launcher.alpha;

import com.hexszeug.werewolf.game.model.authorization.AuthorizationRepository;

/**
 * The {@code MutableAuthorizationRepository} interface represents a repository's interface for adding and removing
 * authorizations.
 */
public interface MutableAuthorizationRepository extends AuthorizationRepository {

    /**
     * Adds an authorization entry to the repository.
     *
     * @param authToken  the authentication token associated with the authorization
     * @param villageId  the identifier of the village associated with the authorization
     * @param playerId   the identifier of the player associated with the authorization
     */
    void addAuthorization(String authToken, String villageId, String playerId);

    /**
     * Removes the authorization entry from the repository based on the provided authentication token.
     *
     * @param authToken  the authentication token associated with the authorization to be removed
     */
    void removeAuthorization(String authToken);
}

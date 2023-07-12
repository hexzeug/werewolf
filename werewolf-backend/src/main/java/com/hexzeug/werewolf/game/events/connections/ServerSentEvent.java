package com.hexzeug.werewolf.game.events.connections;

import com.hexzeug.werewolf.game.model.player.Player;

import java.util.Locale;

/**
 * Published events implementing this interface will automatically be sent to the target players
 * via server-sent events (if the player has an active sse connection).
 * To determine the target players {@link #isTarget(Player)} is called for every player
 * in the village with the village id returned by {@link #getVillageId()}
 * who have an active connection to the server.
 * All players for which {@code isTarget(Player)} returned true (defaults to every player) are considered targets
 * and are sent the payload returned by {@link #getPayload()} with the event name of {@link #getName()}.
 * @param <P> the type of the payload.
 *           Should be serializable by Jackson.
 */
public interface ServerSentEvent<P> {
    /**
     * Used to get the name of the event.
     * Should return the same value for all instances of the same class.
     * The default implementation returns the class name in lowercase.
     * @return the name of the event
     */
    default String getName() {
        return getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    /**
     * Used to get the payload of the event.
     * The type of the payload can be any object as long as it is serializable by Jackson.
     * @return the payload
     */
    P getPayload();

    /**
     * Used to get the village id of the village whose players should receive the event.
     * To specify further which players in the village should receive the event see {@link #isTarget(Player)}.
     * @return the village id
     */
    String getVillageId();

    /**
     * Used to check if a player should receive the event.
     * Is called for every player in the village specified by {@link #getVillageId()}
     * who has an active sse connection.
     * Should return if the player should receive the event or not.
     * @param player the player
     * @return if the player should receive the event
     */
    default boolean isTarget(Player player) {
        return true;
    }
}

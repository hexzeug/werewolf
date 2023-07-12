package com.hexzeug.werewolf.game.connections;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.events.connections.DisconnectingEvent;
import com.hexzeug.werewolf.game.events.connections.ServerSentEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * The {@code ConnectionService} is used to handle {@link Connection Connections}.
 * It can be used to connect or disconnect a player.
 * For sending events to a player please look at
 * {@link ServerSentEvent ServerSentEvent}.
 */
public interface ConnectionService {
    /**
     * Should only be called by request handler methods.
     * Returns a SseEmitter which must be used as the response body
     * of the request to open a sse-connection to the player.
     * <p>
     *     If the player was already connected the old connection is closed
     *     with a {@link ConnectedFromOtherLocationException}.
     * </p>
     * @param player the player requesting the connection
     * @return the sse emitter which must be returned as the response body to the player's client
     */
    SseEmitter connect(Player player);

    /**
     * Disconnects the player using {@link Connection#complete()}.
     * Does nothing if the player is not connected.
     * @param player the player
     */
    void disconnect(Player player);

    /**
     * Disconnects the player using {@link Connection#completeWithError(Throwable)}.
     * Does nothing if the player is not connected.
     * <p>
     *     The cause will be send to the player in advance making use of the
     *     {@link DisconnectingEvent DisconnectingEvent}.
     *     If the exception implements {@link ServerSentError} the serialization will contain more detail.
     * </p>
     * @see ServerSentError
     * @param player the player
     * @param cause the exception passed to the connection as a reason to close the connection
     */
    void disconnect(Player player, Throwable cause);
}

package com.hexzeug.werewolf.game.connections;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.events.connections.ConnectionLifecycleEvent;
import com.hexzeug.werewolf.game.events.connections.DisconnectingEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * A {@code Connection} represents a one-way server to client event stream using
 * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events">server-sent events</a>.
 * The client is identified by its corresponding {@link Player}.
 * <p>
 *     Use the {@link ConnectionService} to initialize connection.
 * </p>
 * <p>
 *     A connection will publish
 *     {@link ConnectionLifecycleEvent ConnectionLifecycleEvents}
 *     during its lifecycle. Please look at the documentation
 *     of {@code ConnectionLifecycleEvent} for further information.
 * </p>
 */
public interface Connection {
    /**
     * Sends the passed sse-event.
     * <p>
     *     If sending fails due to a network error the connection is closed with no exception.
     *     If the connection is already completed an {@code IllegalStateException} is thrown.
     *     You may check if it is completed with {@link Connection#isCompleted()}.
     * </p>
     *
     * @param eventBuilder the sse-event
     * @throws IllegalStateException if the connection is already completed
     */
    void send(SseEmitter.SseEventBuilder eventBuilder);

    /**
     * Completes the connection without notifying the client.
     * Does nothing if the connection is already completed.
     * If you need to notify the client use {@link Connection#completeWithError(Throwable)}.
     * <p>
     *     Before the connection is closed a
     *     {@link DisconnectingEvent DisconnectingEvent}
     *     is published in addition to the {@code DisconnectedEvent}
     *     which is published after the connection closed.
     * </p>
     */
    void complete();

    /**
     * Completes the connection by sending the exception to the client and then closing the connection.
     * Does nothing if the connection is already closed.
     * If you don't want to notify the client use {@link Connection#complete()}.
     * <p>
     *     Before the connection is closed a
     *     {@link DisconnectingEvent DisconnectingEvent}
     *     is published in addition to the {@code DisconnectedEvent}
     *     which is published after the connection closed.
     *     The {@code DisconnectingEvent} will trigger an event handler in {@link ConnectionService} which
     *     serializes the error using {@link ServerSentError.Data} and sends it to the client before
     *     the connection is closed.
     * </p>
     * @see ServerSentError
     * @see ConnectionService#disconnect(Player, Throwable)
     *
     * @param ex the exception serialized and send to the client
     */
    void completeWithError(Throwable ex);

    /**
     * Retrieves the player associated with the connection.
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * Checks if the connection has been completed.
     *
     * @return true if the connection has been completed, false otherwise.
     */
    boolean isCompleted();
}

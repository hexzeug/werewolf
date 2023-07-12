package com.hexzeug.werewolf.game.events.connections;

import com.hexzeug.werewolf.game.connections.Connection;

/**
 * Is fired before a {@link Connection Connection} closes.
 * At that state, the closing cannot be canceled,
 * but the connection can still be used to send information about the connection closing to the client.
 * This event may contain an exception as the cause of the closure.
 */
public interface DisconnectingEvent extends ConnectionLifecycleEvent {
    /**
     * Returns the cause of the closure or {@code null} if there was no cause specified.
     * @return the cause
     */
    Throwable getCause();
}

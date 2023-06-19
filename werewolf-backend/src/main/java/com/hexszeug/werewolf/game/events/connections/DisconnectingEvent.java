package com.hexszeug.werewolf.game.events.connections;

/**
 * Is fired before a {@link com.hexszeug.werewolf.game.connections.Connection Connection} closes.
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

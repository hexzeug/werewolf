package com.hexszeug.werewolf.game.events.connections;

/**
 * Is fired after a {@link com.hexszeug.werewolf.game.connections.Connection Connection} was closed.
 * May contain an exception as the cause of the closure.
 */
public interface DisconnectedEvent extends ConnectionLifecycleEvent {
    /**
     * Returns the cause of the closure or {@code null} if there was no cause specified.
     * @return the cause
     */
    Throwable getCause();
}

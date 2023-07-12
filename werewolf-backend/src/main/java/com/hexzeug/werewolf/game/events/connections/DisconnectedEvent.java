package com.hexzeug.werewolf.game.events.connections;

import com.hexzeug.werewolf.game.connections.Connection;

/**
 * Is fired after a {@link Connection Connection} was closed.
 * May contain an exception as the cause of the closure.
 */
public interface DisconnectedEvent extends ConnectionLifecycleEvent {
    /**
     * Returns the cause of the closure or {@code null} if there was no cause specified.
     * @return the cause
     */
    Throwable getCause();
}

package com.hexzeug.werewolf.game.events.connections;

import com.hexzeug.werewolf.game.connections.Connection;

/**
 * ConnectionLifecycleEvents are published at creation and completion of a {@link Connection}.
 * They hold the corresponding connection.
 * @see ConnectedEvent
 * @see DisconnectingEvent
 * @see DisconnectedEvent
 */
public interface ConnectionLifecycleEvent {
    /**
     * Can be used to access the connection this event was triggered by.
     * @return the connection
     */
    Connection getConnection();
}

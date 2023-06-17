package com.hexszeug.werewolf.game.events.connections;

import com.hexszeug.werewolf.game.connections.Connection;

public interface ConnectionLifecycleEvent {
    Connection getConnection();
}

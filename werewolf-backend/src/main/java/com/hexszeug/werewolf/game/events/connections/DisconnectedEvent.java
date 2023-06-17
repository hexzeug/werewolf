package com.hexszeug.werewolf.game.events.connections;

public interface DisconnectedEvent extends ConnectionLifecycleEvent {
    Throwable getCause();
}

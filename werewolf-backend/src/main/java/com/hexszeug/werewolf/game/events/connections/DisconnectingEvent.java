package com.hexszeug.werewolf.game.events.connections;

public interface DisconnectingEvent extends ConnectionLifecycleEvent {
    Throwable getCause();
}

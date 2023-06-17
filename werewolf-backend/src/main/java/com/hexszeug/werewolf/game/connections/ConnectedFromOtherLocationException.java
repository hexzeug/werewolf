package com.hexszeug.werewolf.game.connections;

import lombok.Getter;

public class ConnectedFromOtherLocationException extends Exception implements ServerSentError {

    @Getter
    private final Connection connection;

    public ConnectedFromOtherLocationException(Connection connection) {
        super();
        this.connection = connection;
    }

    public ConnectedFromOtherLocationException(Connection connection, String message) {
        super(message);
        this.connection = connection;
    }

    public ConnectedFromOtherLocationException(Connection connection, Throwable cause) {
        super(cause);
        this.connection = connection;
    }

    public ConnectedFromOtherLocationException(Connection connection, String message, Throwable cause) {
        super(message, cause);
        this.connection = connection;
    }
}

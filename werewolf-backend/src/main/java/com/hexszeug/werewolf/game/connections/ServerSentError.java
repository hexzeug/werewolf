package com.hexszeug.werewolf.game.connections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hexszeug.werewolf.game.model.player.Player;
import lombok.*;

import java.util.Locale;

/**
 * Interface which may be implemented by exceptions
 * which are passed to {@link ConnectionService#disconnect(Player, Throwable)}.
 * Implementing this with a non subclass of {@code Throwable} is not intended.
 * If an exception implements this interface the name of the serialized JSON object send to the client
 * will be the return value of {@link ServerSentError#getName()}
 * which defaults to the class name in lowercase without the '{@code Exception}' suffix.
 * Other exception will have '{@code internal}' as their name.
 * <p>
 *     <b>Important note:</b> This is only used over server-sent events.
 *     Normal HTTP request use another exception serialization strategy.
 * </p>
 * @see ConnectionService#disconnect(Player, Throwable)
 */
public interface ServerSentError {
    /**
     * Provides the name used in serialization of this error.
     * Should always return the same value for the same class.
     * The default implementation returns the class name in lowercase with the '{@code Exception}' suffix removed.
     * @return the name of the error
     */
    default String getName() {
        final String EXCEPTION = "Exception";
        String name = getClass().getSimpleName();
        if (name.endsWith(EXCEPTION)) {
            name = name.substring(0, name.length() - EXCEPTION.length());
        }
        return name.toLowerCase(Locale.ROOT);
    }

    /**
     * Is used to serialize exceptions for sending them to the client.
     * Look at {@link Data#Data(Throwable)} for more detailed information on how this works.
     */
    @Value
    @AllArgsConstructor(access = AccessLevel.NONE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Data {
        String name;
        String message;
        Data cause;

        /**
         * Creates a new {@code ServerSentError.Data}
         * containing a name, the message and the cause of the passed exception.
         * The message is just copied from the exception.
         * The cause is wrapped in another instance of {@code ServerSentError.Data}.
         * The name defaults to '{@code internal}'. If the exception implements {@link ServerSentError}
         * the name is instead derived from {@link ServerSentError#getName()}.
         * <p>
         *     For the last step of serializing any Jackson is used,
         *     as {@code ServerSentError.Data} instances only contains fields which should be serialized.
         * </p>
         * @param ex the exception to serialize
         */
        public Data(Throwable ex) {
            name = (ex instanceof ServerSentError error) ? error.getName() : "internal";
            message = ex.getMessage();
            cause = ex.getCause() != null ? new Data(ex.getCause()) : null;
        }
    }
}

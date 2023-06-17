package com.hexszeug.werewolf.game.connections;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Locale;

public interface ServerSentError {
    default String getName() {
        final String EXCEPTION = "Exception";
        String name = getClass().getSimpleName();
        if (name.endsWith(EXCEPTION)) {
            name = name.substring(0, name.length() - EXCEPTION.length());
        }
        return name.toLowerCase(Locale.ROOT);
    }
    String getMessage();
    Throwable getCause();

    @Value
    @AllArgsConstructor(access = AccessLevel.NONE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Data {
        String name;
        String message;
        Data cause;

        public Data(Throwable ex) {
            name = (ex instanceof ServerSentError error) ? error.getName() : "internal";
            message = ex.getMessage();
            cause = ex.getCause() != null ? new Data(ex.getCause()) : null;
        }
    }
}

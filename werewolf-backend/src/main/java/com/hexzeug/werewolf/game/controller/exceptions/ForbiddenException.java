package com.hexzeug.werewolf.game.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ForbiddenException extends ResponseStatusException {
    @SuppressWarnings("unused")
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("unused")
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    @SuppressWarnings("unused")
    public ForbiddenException(Throwable cause) {
        super(HttpStatus.FORBIDDEN, null, cause);
    }

    @SuppressWarnings("unused")
    public ForbiddenException(String message, Throwable cause) {
        super(HttpStatus.FORBIDDEN, message, cause);
    }
}

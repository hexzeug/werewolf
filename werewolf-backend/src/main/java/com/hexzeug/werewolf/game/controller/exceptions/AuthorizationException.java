package com.hexzeug.werewolf.game.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationException extends ResponseStatusException {
    @SuppressWarnings("unused")
    public AuthorizationException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("unused")
    public AuthorizationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    @SuppressWarnings("unused")
    public AuthorizationException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, null, cause);
    }

    @SuppressWarnings("unused")
    public AuthorizationException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }
}

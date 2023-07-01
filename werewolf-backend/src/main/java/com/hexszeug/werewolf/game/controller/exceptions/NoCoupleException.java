package com.hexszeug.werewolf.game.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoCoupleException extends ResponseStatusException {
    @SuppressWarnings("unused")
    public NoCoupleException() {
        super(HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unused")
    public NoCoupleException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    @SuppressWarnings("unused")
    public NoCoupleException(Throwable cause) {
        super(HttpStatus.NOT_FOUND, null, cause);
    }

    @SuppressWarnings("unused")
    public NoCoupleException(String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND, message, cause);
    }
}

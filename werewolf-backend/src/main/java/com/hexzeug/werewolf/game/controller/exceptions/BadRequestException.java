package com.hexzeug.werewolf.game.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
    @SuppressWarnings("unused")
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("unused")
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    @SuppressWarnings("unused")
    public BadRequestException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, null, cause);
    }

    @SuppressWarnings("unused")
    public BadRequestException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}

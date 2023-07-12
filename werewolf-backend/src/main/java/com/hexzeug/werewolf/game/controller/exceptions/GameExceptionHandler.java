package com.hexzeug.werewolf.game.controller.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice("com.hexzeug.werewolf.game.controller")
public class GameExceptionHandler extends ResponseEntityExceptionHandler {}

package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.connections.ConnectionService;
import com.hexszeug.werewolf.game.model.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventStreamController {
    private final ConnectionService connectionService;

    @GetMapping("/event-stream")
    public void handle() {
        throw new NotAcceptableStatusException(List.of(MediaType.TEXT_EVENT_STREAM));
    }

    @GetMapping(value = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handle(Player player) {
        return connectionService.connect(player);
    }
}

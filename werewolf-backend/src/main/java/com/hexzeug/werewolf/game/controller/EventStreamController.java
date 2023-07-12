package com.hexzeug.werewolf.game.controller;

import com.hexzeug.werewolf.game.connections.ConnectionService;
import com.hexzeug.werewolf.game.model.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventStreamController {
    private final ConnectionService connectionService;

    @GetMapping(value = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handle(Player player) {
        return connectionService.connect(player);
    }
}

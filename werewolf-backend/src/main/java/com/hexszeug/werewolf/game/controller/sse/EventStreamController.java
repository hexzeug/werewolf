package com.hexszeug.werewolf.game.controller.sse;

import com.hexszeug.werewolf.game.connections.ConnectionService;
import com.hexszeug.werewolf.game.events.connections.ConnectedEvent;
import com.hexszeug.werewolf.game.events.connections.DisconnectedEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
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

    @EventListener
    public void handleConnect(ConnectedEvent event) {
        log.info(String.format("Player %s connected.", event.getConnection().getPlayer().getName()));
    }

    @EventListener
    public void handleDisconnect(DisconnectedEvent event) {
        log.info(String.format("Player %s disconnected.", event.getConnection().getPlayer().getName()));
        if (event.getCause() != null) {
            log.error("onError: " + event.getCause().toString());
        }
    }

    @GetMapping("/test")
    public String test(Player player) {
        connectionService.disconnect(player);
        return String.format("You are %s.", player.getName());
    }
}

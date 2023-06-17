package com.hexszeug.werewolf.game.connections;

import com.hexszeug.werewolf.game.model.player.Player;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ConnectionService {
    SseEmitter connect(Player player);

    void disconnect(Player player);

    void disconnect(Player player, Throwable cause);
}

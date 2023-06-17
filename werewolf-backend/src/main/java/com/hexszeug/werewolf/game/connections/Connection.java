package com.hexszeug.werewolf.game.connections;

import com.hexszeug.werewolf.game.model.player.Player;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface Connection {
    SseEmitter initialize();
    void send(SseEmitter.SseEventBuilder eventBuilder);
    void complete();
    void completeWithError(Throwable ex);
    Player getPlayer();
    boolean isCompleted();
}

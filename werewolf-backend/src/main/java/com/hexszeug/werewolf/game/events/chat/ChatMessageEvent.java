package com.hexszeug.werewolf.game.events.chat;

import com.hexszeug.werewolf.game.controller.ChatController;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import lombok.Value;

@Value
public class ChatMessageEvent implements ServerSentEvent<ChatController.ChatMessage> {
    ChatController.ChatMessage payload;
    String villageId;
}

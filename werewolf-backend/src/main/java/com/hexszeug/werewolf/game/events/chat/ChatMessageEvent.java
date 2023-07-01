package com.hexszeug.werewolf.game.events.chat;

import com.hexszeug.werewolf.game.controller.ChatController;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import lombok.Value;

/**
 * Published to all players when a chat message is sent.
 * <b>Payload:</b>
 * <pre>
 * {
 *     author: {@link String} (player id)
 *     message: {@link String}
 *     timestamp: {@code long}
 * }
 * </pre>
 */
@Value
public class ChatMessageEvent implements ServerSentEvent<ChatController.ChatMessage> {
    ChatController.ChatMessage payload;
    String villageId;
}

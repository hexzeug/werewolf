package com.hexszeug.werewolf.game.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.events.chat.ChatMessageEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private static final String KEY_CHAT = "chat";

    private final ApplicationEventPublisher eventPublisher;

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <p>
     *     none
     * </p>
     * <b>Response:</b>
     * <pre>
     * [
     *     {
     *         author: {@link String} (player id)
     *         message: {@link String}
     *         timestamp: {@code long}
     *     }
     *     ...
     * ]
     * </pre>
     */
    @GetMapping("/chat")
    public List<ChatMessage> handleGet(Village village) {
        return getChat(village);
    }

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ol>
     *     <li>the current phase is accusation</li>
     *     <li>the player is alive</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * {@link String} (message)
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 201 CREATED}
     * </p>
     * <b>Effects:</b>
     * <ol>
     *     <li>add chat message</li>
     *     <li>publish a {@link ChatMessageEvent}</li>
     * </ol>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePost(@RequestBody TextNode messageTextNode, Player player, Village village) {
        if (village.getCurrentPhase() != Phase.ACCUSATION) {
            throw new ForbiddenException("The phase must be accusation.");
        }
        if (!player.isAlive()) {
            throw new ForbiddenException("You must be alive.");
        }
        ChatMessage message = new ChatMessage(
                player.getPlayerId(),
                messageTextNode.asText(),
                System.currentTimeMillis()
        );
        List<ChatMessage> chat = new ArrayList<>(getChat(village));
        chat.add(message);
        village.set(KEY_CHAT, chat);
        eventPublisher.publishEvent(new ChatMessageEvent(message, village.getVillageId()));
    }

    private List<ChatMessage> getChat(Village village) {
        try {
            List<?> chat = village.get(KEY_CHAT, List.class);
            if (chat == null) return Collections.emptyList();
            return chat
                    .stream()
                    .map(elm -> {
                        //TODO wont work if custom props are serialized to json
                        if (!(elm instanceof ChatMessage message)) {
                            throw new IllegalStateException("Chat contains non message.");
                        }
                        return message;
                    })
                    .toList();
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Chat contains non list.", ex);
        }
    }

    @Value
    public static class ChatMessage {
        String author;
        String message;
        long timestamp;
    }
}

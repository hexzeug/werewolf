package com.hexszeug.werewolf.game.events.connections;

import com.hexszeug.werewolf.game.model.player.Player;

import java.util.Locale;

public interface ServerSentEvent<P> {
    default String getName() {
        return getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    P getPayload();

    String getVillageId();

    default boolean isTarget(Player player) {
        return true;
    };
}

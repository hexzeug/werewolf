package com.hexszeug.werewolf.game.events.werewolf;

import com.hexszeug.werewolf.game.controller.WerewolfController;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import lombok.Value;

/**
 * Published to all werewolves of the village when a werewolf changes their vote.
 * <b>Payload:</b>
 * <pre>
 * {
 *     voter: {@link String} (player id)
 *     vote: {@code null} or {@link String} (player id)
 * }
 * </pre>
 */
@Value
public class WerewolfVoteEvent implements ServerSentEvent<WerewolfController.VoteInfo> {
    WerewolfController.VoteInfo payload;
    String villageId;

    @Override
    public boolean isTarget(Player player) {
        return player.getRole() == Role.WEREWOLF;
    }
}

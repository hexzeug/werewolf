package com.hexszeug.werewolf.game.events.court;


import com.hexszeug.werewolf.game.controller.CourtController;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import lombok.Value;

/**
 * Published to all players when a player votes for an accused during court phase.
 * <b>Payload:</b>
 * <pre>
 * {
 *     voter: {@link String} (player id)
 *     vote: {@link String} (player id) (cannot be null)
 * }
 * </pre>
 */
@Value
public class CourtVoteEvent implements ServerSentEvent<CourtController.VoteInfo> {
    CourtController.VoteInfo payload;
    String villageId;
}

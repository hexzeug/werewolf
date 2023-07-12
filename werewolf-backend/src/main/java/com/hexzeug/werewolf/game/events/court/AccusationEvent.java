package com.hexzeug.werewolf.game.events.court;


import com.hexzeug.werewolf.game.controller.CourtController;
import com.hexzeug.werewolf.game.events.connections.ServerSentEvent;
import lombok.Value;

/**
 * Published to all players if a player changes who he accuses.
 * <p>Payload:</p>
 * <pre>
 * {
 *     accuser: {@link String} (player id)
 *     accused: {@code null} or {@link String} (player id)
 * }
 * </pre>
 */
@Value
public class AccusationEvent implements ServerSentEvent<CourtController.AccusationInfo> {
    CourtController.AccusationInfo payload;
    String villageId;
}

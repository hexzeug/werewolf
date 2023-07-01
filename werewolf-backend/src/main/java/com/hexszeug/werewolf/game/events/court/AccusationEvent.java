package com.hexszeug.werewolf.game.events.court;


import com.hexszeug.werewolf.game.controller.CourtController;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import lombok.Value;

@Value
public class AccusationEvent implements ServerSentEvent<CourtController.AccusationInfo> {
    CourtController.AccusationInfo payload;
    String villageId;
}

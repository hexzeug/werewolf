package com.hexzeug.werewolf.game.events.phase;

import com.hexzeug.werewolf.game.events.connections.ServerSentEvent;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import lombok.Value;

@Value
public class PhaseEvent implements ServerSentEvent<Phase> {
    Village village;
    Phase phase;

    public PhaseEvent(Village village) {
        this.village = village;
        phase = village.getCurrentPhase();
    }

    @Override
    public Phase getPayload() {
        return phase;
    }

    @Override
    public String getVillageId() {
        return village.getVillageId();
    }
}

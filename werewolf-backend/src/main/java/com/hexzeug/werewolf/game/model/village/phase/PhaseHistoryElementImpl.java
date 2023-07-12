package com.hexzeug.werewolf.game.model.village.phase;

import lombok.Value;

@Value
public class PhaseHistoryElementImpl implements PhaseHistoryElement {
    Phase phase;
    long timestamp;
}

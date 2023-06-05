package com.hexszeug.werewolf.game.model.village.phase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PhaseHistoryElementImplTest {

    @Test
    void storesPhase() {
        PhaseHistoryElementImpl phaseHistoryElement = new PhaseHistoryElementImpl(Phase.HUNTER, 0);
        assertThat(phaseHistoryElement.getPhase()).isEqualTo(Phase.HUNTER);
    }

    @Test
    void storesTimestamp() {
        PhaseHistoryElementImpl phaseHistoryElement = new PhaseHistoryElementImpl(Phase.WITCH_POISON, 3141592);
        assertThat(phaseHistoryElement.getTimestamp()).isEqualTo(3141592);
    }
}
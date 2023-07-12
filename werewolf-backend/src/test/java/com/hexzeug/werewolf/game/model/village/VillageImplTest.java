package com.hexzeug.werewolf.game.model.village;

import com.hexzeug.werewolf.game.model.customprops.CustomPropertiesTestUtils;
import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import com.hexzeug.werewolf.game.model.village.phase.PhaseHistoryElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class VillageImplTest {
    @Test
    void comparesById(@Mock Player player1, @Mock Player player2) {
        VillageImpl village1 = new VillageImpl(
                "test-village-1",
                List.of(Phase.SEER, Phase.ACCUSATION),
                List.of(player1)
        );
        VillageImpl village2 = new VillageImpl(
                "test-village-2",
                List.of(Phase.WITCH_HEAL),
                List.of(player2, player1)
        );
        VillageImpl village = new VillageImpl(
                "test-village-1",
                List.of(Phase.CUPID, Phase.COURT),
                List.of(player1, player2)
        );
        assertThat(village1).isEqualTo(village);
        assertThat(village2).isNotEqualTo(village);
    }

    @Test
    void providesPhaseOrder() {
        VillageImpl village = new VillageImpl(
                "test-village",
                List.of(Phase.CUPID, Phase.SEER, Phase.WEREWOLVES),
                Collections.emptyList()
        );
        assertThat(village.getPhaseOrder()).containsExactly(Phase.CUPID, Phase.SEER, Phase.WEREWOLVES);
        assertThat(village.getPhaseOrderIndex()).isEqualTo(-1);
        village.setPhaseOrderIndex(2);
        assertThat(village.getPhaseOrderIndex()).isEqualTo(2);
        village.setPhaseOrderIndex(0);
        assertThat(village.getPhaseOrderIndex()).isEqualTo(0);
    }

    @Test
    void providesInGameTime() {
        VillageImpl village = new VillageImpl(
                "test-village",
                Collections.emptyList(),
                Collections.emptyList()
        );
        assertThat(village.getIGTime()).isEqualTo(0);
        village.incrementIGTime();
        assertThat(village.getIGTime()).isEqualTo(1);
        village.incrementIGTime();
        assertThat(village.getIGTime()).isEqualTo(2);
        village.incrementIGTime();
        assertThat(village.getIGTime()).isEqualTo(3);
    }

    @Test
    void providesPhaseHistory(@Mock PhaseHistoryElement phaseHistoryElement) {
        VillageImpl village = new VillageImpl(
                "test-village",
                Collections.emptyList(),
                Collections.emptyList()
        );
        assertThat(village.getPhaseHistory()).hasSize(1);
        village.pushPhaseHistoryElement(phaseHistoryElement);
        assertThat(village.getPhaseHistory()).hasSize(2).contains(phaseHistoryElement);
    }

    @Test
    void queriesPlayerById(@Mock Player player1, @Mock Player player2) {
        when(player1.getPlayerId()).thenReturn("test-player-1");
        when(player2.getPlayerId()).thenReturn("test-player-2");
        VillageImpl village = new VillageImpl(
                "test-village",
                Collections.emptyList(),
                List.of(player1, player2)
        );
        assertThat(village.getPlayerById("test-player-1")).isEqualTo(player1);
        assertThat(village.getPlayerById("test-player-2")).isEqualTo(player2);
        assertThat(village.getPlayerById("does-not-exist")).isNull();
        assertThat(village.getPlayerList()).containsExactly(player1, player2);
    }

    @Test
    void storesCustomProperties() {
        VillageImpl village = new VillageImpl(
                "test-village",
                Collections.emptyList(),
                Collections.emptyList()
        );
        assertThat(village.get("test-key-a", Object.class)).isNull();
        assertThat(village.get("test-key-b", Object.class)).isNull();
        village.set("test-key-a", CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(village.get("test-key-a", CustomPropertiesTestUtils.TYPE_CUSTOM))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(village.get("test-key-b", Object.class)).isNull();
        village.set("test-key-b", CustomPropertiesTestUtils.VALUE_OBJECT);
        assertThat(village.get("test-key-a", CustomPropertiesTestUtils.TYPE_CUSTOM))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(village.get("test-key-b", Object.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_OBJECT);
        village.set("test-key-a", CustomPropertiesTestUtils.VALUE_STRING);
        assertThat(village.get("test-key-a", String.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_STRING);
        assertThat(village.get("test-key-b", Object.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_OBJECT);
        village.delete("test-key-a");
        assertThat(village.get("test-key-a", Object.class)).isNull();
        assertThat(village.get("test-key-b", Object.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_OBJECT);
    }
}
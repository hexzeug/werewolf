package com.hexzeug.werewolf.game.model.player;

import com.hexzeug.werewolf.game.model.customprops.CustomPropertiesTestUtils;
import com.hexzeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexzeug.werewolf.game.model.player.role.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlayerImplTest {
    @Test
    void comparesById() {
        PlayerImpl player1 = new PlayerImpl(
                "test-player-1",
                "test-village-1",
                "test-name",
                Role.WITCH
        );
        PlayerImpl player2 = new PlayerImpl(
                "test-player-2",
                "test-village-2",
                "test-name",
                Role.WEREWOLF
        );
        PlayerImpl player = new PlayerImpl(
                "test-player-1",
                "test-village-3",
                "test-name",
                Role.SEER
        );
        assertThat(player1).isEqualTo(player);
        assertThat(player2).isNotEqualTo(player);
    }

    @Test
    void providesVillageId() {
        PlayerImpl player = new PlayerImpl(
                "test-player",
                "test-village",
                "test-name",
                Role.VILLAGER
        );
        assertThat(player.getVillageId()).isEqualTo("test-village");
    }

    @Test
    void providesIdentity() {
        PlayerImpl player = new PlayerImpl(
                "test-player",
                "test-village",
                "test-name",
                Role.HUNTER
        );
        assertThat(player.getName()).isEqualTo("test-name");
        Assertions.assertThat(player.getRole()).isEqualTo(Role.HUNTER);
    }

    @Test
    void storesAliveStatus() {
        PlayerImpl player = new PlayerImpl(
                "test-player",
                "test-village",
                "test-name",
                Role.VILLAGER
        );
        assertThat(player.isAlive()).isEqualTo(true);
        player.setAlive(false);
        assertThat(player.isAlive()).isEqualTo(false);
        player.setAlive(true);
        assertThat(player.isAlive()).isEqualTo(true);
    }

    @Test
    void storesDeathInfo() {
        PlayerImpl player = new PlayerImpl(
                "test-player",
                "test-village",
                "test-name",
                Role.VILLAGER
        );
        player.setDeathIGTime(10);
        player.setDeathReason(DeathReason.NIGHT);
        Assertions.assertThat(player.getDeathReason()).isEqualTo(DeathReason.NIGHT);
        assertThat(player.getDeathIGTime()).isEqualTo(10);
    }

    @Test
    void storesCustomProperties() {
        PlayerImpl player = new PlayerImpl(
                "test-player",
                "test-village",
                "test-name",
                Role.CUPID
        );
        assertThat(player.get("test-key-a", Object.class)).isNull();
        assertThat(player.get("test-key-b", Object.class)).isNull();
        player.set("test-key-a", CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(player.get("test-key-a", CustomPropertiesTestUtils.TYPE_CUSTOM))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(player.get("test-key-b", Object.class)).isNull();
        player.set("test-key-b", CustomPropertiesTestUtils.VALUE_INT);
        assertThat(player.get("test-key-a", CustomPropertiesTestUtils.TYPE_CUSTOM))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_CUSTOM);
        assertThat(player.get("test-key-b", Integer.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_INT);
        player.set("test-key-a", CustomPropertiesTestUtils.VALUE_SERIALIZABLE);
        assertThat(player.get("test-key-a", CustomPropertiesTestUtils.TYPE_SERIALIZABLE))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_SERIALIZABLE);
        assertThat(player.get("test-key-b", Integer.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_INT);
        player.delete("test-key-a");
        assertThat(player.get("test-key-a", Object.class)).isNull();
        assertThat(player.get("test-key-b", Integer.class))
                .isEqualTo(CustomPropertiesTestUtils.VALUE_INT);
    }
}
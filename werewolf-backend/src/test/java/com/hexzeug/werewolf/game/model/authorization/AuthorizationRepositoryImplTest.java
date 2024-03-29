package com.hexzeug.werewolf.game.model.authorization;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.VillageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthorizationRepositoryImplTest {
    @Autowired
    private AuthorizationRepositoryImpl authorizationRepository;

    @MockBean
    private VillageRepository villageRepository;
    @Mock
    private Village village;
    @Mock
    private Player player;

    @BeforeEach
    void prepareMocks() {
        when(villageRepository.getByVillageId(any())).thenReturn(village);
        when(village.getPlayerById(any())).thenReturn(player);
    }

    @Test
    void providesPlayerByAuthToken() {
        authorizationRepository.addAuthorization("test-token", "test-village", "test-player");
        Assertions.assertThat(authorizationRepository.getPlayerByAuthToken("test-token")).isEqualTo(player);
        verify(villageRepository).getByVillageId("test-village");
        verify(village).getPlayerById("test-player");
    }

    @Test
    void removesAuthorization() {
        authorizationRepository.addAuthorization("test-token", "test-village", "test-player");
        authorizationRepository.removeAuthorization("test-token");
        Assertions.assertThat(authorizationRepository.getPlayerByAuthToken("test-token")).isNull();
    }
}
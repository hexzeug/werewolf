package com.hexszeug.werewolf.game.model.village;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class VillageRepositoryImplTest {
    @Autowired
    private VillageRepositoryImpl villageRepository;

    @Mock
    private Village village;

    @BeforeEach
    void prepareMocks() {
        when(village.getVillageId()).thenReturn("test-village");
    }

    @Test
    void providesVillageById() {
        villageRepository.addVillage(village);
        assertThat(villageRepository.getByVillageId("test-village")).isEqualTo(village);
    }

    @Test
    void removesVillage() {
        villageRepository.addVillage(village);
        villageRepository.removeVillage(village);
        assertThat(villageRepository.getByVillageId("test-village")).isNull();
    }
}
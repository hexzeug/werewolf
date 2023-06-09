package com.hexszeug.werewolf;

import com.hexszeug.werewolf.launcher.alpha.GameCreationService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AlphaLauncherDisableConfiguration {
    @MockBean
    private GameCreationService gameCreationService;
}
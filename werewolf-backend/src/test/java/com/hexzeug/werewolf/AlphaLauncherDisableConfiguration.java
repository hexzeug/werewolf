package com.hexzeug.werewolf;

import com.hexzeug.werewolf.launcher.alpha.GameCreationService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AlphaLauncherDisableConfiguration {
    @MockBean
    private GameCreationService gameCreationService;
}
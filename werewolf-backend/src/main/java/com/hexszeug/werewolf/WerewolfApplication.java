package com.hexszeug.werewolf;

import com.hexszeug.werewolf.launcher.alpha.GameCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;

@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class WerewolfApplication {
	private final GameCreationService alphaGameCreationService;

	public static void main(String[] args) {
		SpringApplication.run(WerewolfApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Async
	private void createAlphaGame() {
		alphaGameCreationService.createGame(6);
	}
}

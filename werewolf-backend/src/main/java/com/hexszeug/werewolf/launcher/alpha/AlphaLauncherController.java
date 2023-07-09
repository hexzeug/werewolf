package com.hexszeug.werewolf.launcher.alpha;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alpha/launcher")
@RequiredArgsConstructor
public class AlphaLauncherController {
    private final GameManager gameManager;

    @PostMapping("/game")
    public List<String> handlePostGame(@RequestParam("players") int playerCount) {
        return gameManager.newRoom(playerCount);
    }

    @GetMapping("/running")
    public boolean handleGetRunning(@CookieValue(value = "wat", required = false) String authToken) {
        return gameManager.isRunning(authToken);
    }

    @PostMapping("/name")
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostName(
            @RequestParam String name,
            @CookieValue(value = "wat", required = false) String authToken
    ) {
        gameManager.setName(authToken, name);
    }
}

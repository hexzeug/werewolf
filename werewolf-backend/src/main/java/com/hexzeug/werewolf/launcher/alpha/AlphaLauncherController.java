package com.hexzeug.werewolf.launcher.alpha;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/alpha/launcher")
@RequiredArgsConstructor
public class AlphaLauncherController {
    private final GameManager gameManager;
    private final Environment environment;

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

    /**
     * enable feedback by adding command line attribute {@code --feedback=true}
     */
    @PostMapping(value = "/feedback", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostFeedback(@RequestBody String feedback, ServletRequest request) throws IOException {
        if (!environment.getProperty("feedback", Boolean.TYPE, false)) return;
        try (FileOutputStream fileOutputStream = new FileOutputStream("feedback.txt", true)) {
            fileOutputStream.write(
                    "%1$td.%1$tm.%1$tY %1$tT: (%2$s)\n%3$s\n\n\n".formatted(
                            new Date(),
                            request.getRemoteAddr(),
                            feedback
                    ).getBytes(StandardCharsets.UTF_8)
            );
        }
    }
}

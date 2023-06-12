package com.hexszeug.werewolf.game.controller.authorization;

import com.hexszeug.werewolf.game.model.authorization.AuthorizationRepository;
import com.hexszeug.werewolf.game.model.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice("com.hexszeug.werewolf.game.controller")
public class AuthorizationProvider {
    private final AuthorizationRepository authorizationRepository;

    @ModelAttribute
    public Player providePlayer(@CookieValue(name = "wat", required = false) String authToken) {
        if (authToken == null) {
            throw new AuthorizationException("Missing authorization token.");
        }
        Player player = authorizationRepository.getPlayerByAuthToken(authToken);
        if (player == null) {
            throw new AuthorizationException("Bad authorization token.");
        }
        return player;
    }
}

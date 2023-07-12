package com.hexzeug.werewolf.game.controller;

import com.hexzeug.werewolf.game.controller.exceptions.AuthorizationException;
import com.hexzeug.werewolf.game.model.authorization.AuthorizationRepository;
import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice("com.hexszeug.werewolf.game.controller")
public class AuthorizationProvider {
    private final AuthorizationRepository authorizationRepository;
    private final VillageRepository villageRepository;

    @ModelAttribute
    public void providePlayerAndVillage(@CookieValue(name = "wat", required = false) String authToken, Model model) {
        if (authToken == null) {
            throw new AuthorizationException("Missing authorization token.");
        }
        Player player = authorizationRepository.getPlayerByAuthToken(authToken);
        if (player == null) {
            throw new AuthorizationException("Bad authorization token.");
        }
        Village village = villageRepository.getByVillageId(player.getVillageId());
        if (village == null) {
            throw new IllegalStateException("Player without village.");
        }
        model.addAttribute("player", player).addAttribute("village", village);
    }
}

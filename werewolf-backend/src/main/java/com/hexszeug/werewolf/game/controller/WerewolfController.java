package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/werewolves")
public class WerewolfController {
    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a werewolf</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {@link String} (player id)
     *     ...
     * ]
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping
    public List<String> handleGetWerewolves(Player player, Village village) {
        if (player.getRole() != Role.WEREWOLF) {
            throw new ForbiddenException("You must be a werewolf.");
        }
        return village.getPlayerList()
                .stream()
                .filter(p -> p.getRole() == Role.WEREWOLF)
                .map(Player::getPlayerId)
                .toList();
    }
}

package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/witch")
public class WitchController {
    private static final String KEY_HEAL = "witchHeal";

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is a witch</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {@code boolean}
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping(value = "/heal", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean handleGetHeal(Player player) {
        if (player.getRole() != Role.WITCH) {
            throw new ForbiddenException("You must be a witch.");
        }
        try {
            Boolean heal = player.get(KEY_HEAL, Boolean.class);
            return heal != null && heal;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Witch heal contains non boolean.", ex);
        }
    }
}

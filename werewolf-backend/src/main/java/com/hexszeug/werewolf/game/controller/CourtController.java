package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CourtController {
    public static final String KEY_ACCUSATION = "accusation";

    /**
     * @apiNote
     * <b>Permissions:</b>
     * <ul>
     *     <li>current phase is accusation</li>
     *     <li>current phase is court</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {
     *         accuser: {@link String} (player id)
     *         accused: {@link String} (player id)
     *     }
     *     ...
     * ]
     * </pre>
     * @throws ForbiddenException if the permissions are not fulfilled
     */
    @GetMapping("/accusations")
    public List<AccusationInfo> handleGetAccusations(Village village) {
        if (village.getCurrentPhase() != Phase.ACCUSATION && village.getCurrentPhase() != Phase.COURT) {
            throw new ForbiddenException("The phase must either be accusation or court.");
        }
        return village.getPlayerList()
                .stream()
                .map(player -> {
                    String accusation = getAccusation(player);
                    if (accusation == null) return null;
                    return new AccusationInfo(player.getPlayerId(), accusation);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private String getAccusation(Player player) {
        try {
            return player.get(KEY_ACCUSATION, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Accusation contains non string.", ex);
        }
    }

    @Value
    private static class AccusationInfo {
        String accuser;
        String accused;
    }
}

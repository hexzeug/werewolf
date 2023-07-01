package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.controller.exceptions.NoCoupleException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CupidController {
    public static final String KEY_COUPLE_MEMBER_1 = "coupleMember1";
    public static final String KEY_COUPLE_MEMBER_2 = "coupleMember2";

    /**
     * <b>Permissions:</b>
     * <ul>
     *     <li>couple is dead</li>
     *     <li>requester is member of couple</li>
     *     <li>requester is the cupid</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * [
     *     {@link String} (player id)
     *     {@link String} (player id)
     * ]
     * </pre>
     * @throws NoCoupleException if no couple exists
     * @throws ForbiddenException if the requester is not permitted
     */
    @GetMapping("/couple")
    public List<String> handleGetCouple(Player player, Village village) {
        if (!hasCouple(village)) {
            throw new NoCoupleException();
        }
        if (getCoupleAlive(village)
                && !isInCouple(player, village)
                && player.getRole() != Role.CUPID
        ) {
            throw new ForbiddenException("The couple must be dead or you must be either in the couple or the cupid.");
        }
        return List.of(getPlayerId1(village), getPlayerId2(village));
    }

    /**
     * <b>Permissions:</b>
     * <ol>
     *     <li>player is the cupid</li>
     *     <li>cupid phase is current phase</li>
     * </ol>
     * <b>Request:</b>
     * <pre>
     * [
     *     {@link String} (player id)
     *     {@link String} (player id)
     * ]
     * </pre>
     * <b>Response:</b>
     * <p>
     *     {@code 201 CREATED}
     * </p>
     * <b>Effects:</b>
     * <ul>
     *     <li>sets couple to players from body</li>
     *     <li>continues narration</li>
     * </ul>
     * @throws ForbiddenException if permissions aren't met
     * @throws BadRequestException if the request body contains more or less than 2 players
     *                             or the passed player ids do not exist
     */
    @PostMapping(value = "/couple", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void handlePostCouple(@RequestBody List<String> coupleIds, Player player, Village village) {
        if (player.getRole() != Role.CUPID) {
            throw new ForbiddenException("You must be the cupid.");
        }
        if (village.getCurrentPhase() != Phase.CUPID) {
            throw new ForbiddenException("Only available in the cupid phase.");
        }
        if (coupleIds.size() != 2) {
            throw new BadRequestException("Couple array must contain 2 elements not " + coupleIds.size() + ".");
        }
        Player player1 = village.getPlayerById(coupleIds.get(0));
        Player player2 = village.getPlayerById(coupleIds.get(1));
        if (player1 == null) {
            throw new BadRequestException("Player %s is not in the village.".formatted(coupleIds.get(0)));
        }
        if (player2 == null) {
            throw new BadRequestException("Player %s is not in the village.".formatted(coupleIds.get(1)));
        }
        village.set(KEY_COUPLE_MEMBER_1, player1.getPlayerId());
        village.set(KEY_COUPLE_MEMBER_2, player2.getPlayerId());
        //TODO continue narration
    }

    /**
     * <b>Permissions:</b>
     * <ul>
     *     <li>player is in the couple</li>
     * </ul>
     * <b>Response:</b>
     * <pre>
     * {
     *     {@code <player id>}: {
     *         role: {@link Role}
     *     }
     *     {@code <player id>}: {
     *         role: {@link Role}
     *     }
     * }
     * </pre>
     */
    @GetMapping("/couple/roles")
    public Map<String, RoleInfo> handleGetCoupleRoles(Player player, Village village) {
        if (!hasCouple(village)) {
            throw new NoCoupleException();
        } else if (!isInCouple(player, village)) {
            throw new ForbiddenException("You must be in the couple.");
        }
        Player player1 = village.getPlayerById(getPlayerId1(village));
        Player player2 = village.getPlayerById(getPlayerId2(village));
        return Map.of(
                player1.getPlayerId(), new RoleInfo(player1.getRole()),
                player2.getPlayerId(), new RoleInfo(player2.getRole())
        );
    }

    private String getPlayerId1(Village village) {
        try {
            return village.get(KEY_COUPLE_MEMBER_1, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couple member is not a string.", ex);
        }
    }

    private String getPlayerId2(Village village) {
        try {
            return village.get(KEY_COUPLE_MEMBER_2, String.class);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couple member is not a string.", ex);
        }
    }

    private Boolean getCoupleAlive(Village village) {
        Player player1 = village.getPlayerById(getPlayerId1(village));
        if (player1 == null) {
            throw new IllegalStateException("Player1 of couple does not exist.");
        }
        return player1.isAlive();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasCouple(Village village) {
        return getPlayerId1(village) != null && getPlayerId2(village) != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isInCouple(Player player, Village village) {
        return player.getPlayerId().equals(getPlayerId1(village))
                || player.getPlayerId().equals(getPlayerId2(village));
    }

    @Value
    private static class RoleInfo {
        Role role;
    }
}

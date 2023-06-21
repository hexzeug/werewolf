package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.BadRequestException;
import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.controller.exceptions.NoCoupleException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CupidController {
    private static final String KEY_COUPLE_MEMBER_1 = "coupleMember1";
    private static final String KEY_COUPLE_MEMBER_2 = "coupleMember2";
    private static final String KEY_COUPLE_ALIVE = "coupleAlive";

    /**
     * @apiNote
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
        String member1 = village.get(KEY_COUPLE_MEMBER_1, String.class);
        String member2 = village.get(KEY_COUPLE_MEMBER_2, String.class);
        Boolean coupleAlive = village.get(KEY_COUPLE_ALIVE, Boolean.class);
        if (member1 == null || member2 == null || coupleAlive == null) {
            throw new NoCoupleException();
        }
        if (player.getRole() != Role.CUPID
                && !player.getPlayerId().equals(member1)
                && !player.getPlayerId().equals(member2)
                && coupleAlive
        ) {
            throw new ForbiddenException("The couple must be dead or you must be either in the couple or the cupid.");
        }
        return List.of(member1, member2);
    }

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
        village.set(KEY_COUPLE_ALIVE, true);
        //TODO continue narration
    }
}

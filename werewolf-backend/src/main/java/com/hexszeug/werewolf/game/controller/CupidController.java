package com.hexszeug.werewolf.game.controller;

import com.hexszeug.werewolf.game.controller.exceptions.ForbiddenException;
import com.hexszeug.werewolf.game.controller.exceptions.NoCoupleException;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

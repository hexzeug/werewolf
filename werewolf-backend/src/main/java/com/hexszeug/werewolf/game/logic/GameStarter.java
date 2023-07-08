package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.events.connections.ConnectedEvent;
import com.hexszeug.werewolf.game.logic.services.NarrationService;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GameStarter {
    private final VillageRepository villageRepository;
    private final NarrationService narrationService;

    private final Map<Village, Set<Player>> connected = new HashMap<>();

    @EventListener
    public void handleConnection(ConnectedEvent event) {
        Player player = event.getConnection().getPlayer();
        Village village = villageRepository.getByVillageId(player.getVillageId());
        if (village.getCurrentPhase() != Phase.GAME_START) return;
        Set<Player> villageConnected = connected.computeIfAbsent(village, (__) -> new HashSet<>());
        villageConnected.add(player);
        if (villageConnected.size() == village.getPlayerList().size()) {
            narrationService.continueNarration(village, Phase.GAME_START);
        }
    }
}

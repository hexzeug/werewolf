package com.hexszeug.werewolf.game.logic;

import com.hexszeug.werewolf.game.events.connections.ConnectedEvent;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GameStartMaster {
    private final VillageRepository villageRepository;
    private final NarrationService narrationService;

    private final Map<Village, Integer> connected = new HashMap<>();

    @EventListener
    public void handleConnection(ConnectedEvent event) {
        Village village = villageRepository.getByVillageId(event.getConnection().getPlayer().getVillageId());
        if (connected.compute(village, (__, count) -> count == null ? 1 : count + 1) == village.getPlayerList().size()) {
            connected.remove(village);
            narrationService.continueNarration(village, Phase.GAME_START);
        }
    }
}

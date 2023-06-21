package com.hexszeug.werewolf.game.model.village;

import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElement;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElementImpl;
import lombok.*;

import java.util.*;

@Data
@RequiredArgsConstructor(access = AccessLevel.NONE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VillageImpl implements Village {
    @EqualsAndHashCode.Include
    private final String villageId;
    private final List<Phase> phaseOrder;
    private int phaseOrderIndex = -1;
    @Setter(AccessLevel.NONE)
    private int IGTime = 0;
    private final List<PhaseHistoryElement> phaseHistory;
    @Getter(AccessLevel.NONE)
    private final Map<String, Object> customProps = new HashMap<>();
    private final List<Player> playerList;

    public VillageImpl(String villageId, List<Phase> phaseOrder, List<Player> players) {
        this.villageId = villageId;
        this.phaseOrder = phaseOrder;
        this.phaseHistory =
                new ArrayList<>(List.of(new PhaseHistoryElementImpl(Phase.GAME_START, System.currentTimeMillis())));
        this.playerList = List.copyOf(players);
    }

    @Synchronized
    @Override
    public void incrementIGTime() {
        IGTime++;
    }

    @Override
    public Phase getCurrentPhase() {
        if (phaseHistory.isEmpty()) return null;
        return phaseHistory.get(phaseHistory.size() - 1).getPhase();
    }

    @Override
    public void pushPhaseHistoryElement(PhaseHistoryElement phaseHistoryElement) {
        if (phaseHistoryElement != null) {
            phaseHistory.add(phaseHistoryElement);
        }
    }

    @Override
    public void set(String key, Object value) {
        customProps.put(key, value);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (!customProps.containsKey(key)) return null;
        return clazz.cast(customProps.get(key));
    }

    @Override
    public void delete(String key) {
        customProps.remove(key);
    }

    @Override
    public Player getPlayerById(String playerId) {
        if (playerId == null) return null;
        for (Player player : playerList) {
            if (playerId.equals(player.getPlayerId())) {
                return player;
            }
        }
        return null;
    }
}

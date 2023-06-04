package com.hexszeug.werewolf.game.model.village;

import com.hexszeug.werewolf.game.model.customprops.CustomProperties;
import com.hexszeug.werewolf.game.model.player.PlayerRepository;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import com.hexszeug.werewolf.game.model.village.phase.PhaseHistoryElement;

import java.util.List;

public interface Village extends CustomProperties, PlayerRepository {
    String getVillageId();
    List<Phase> getPhaseOrder();
    int getPhaseOrderIndex();
    void setPhaseOrderIndex(int phaseOrderIndex);
    int getIGTime();
    void incrementIGTime();
    List<PhaseHistoryElement> getPhaseHistory();
    void pushPhaseHistoryElement(PhaseHistoryElement phaseHistoryElement);
}

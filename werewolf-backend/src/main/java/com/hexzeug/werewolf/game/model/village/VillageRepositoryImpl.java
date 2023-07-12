package com.hexzeug.werewolf.game.model.village;

import com.hexzeug.werewolf.launcher.alpha.MutableVillageRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class VillageRepositoryImpl implements VillageRepository, MutableVillageRepository {
    private final Map<String, Village> villageMap = new HashMap<>();

    @Override
    public Village getByVillageId(String villageId) {
        if (villageId == null) return null;
        return villageMap.get(villageId);
    }

    public void addVillage(Village village) {
        villageMap.put(village.getVillageId(), village);
    }

    public void removeVillage(Village village) {
        villageMap.remove(village.getVillageId());
    }
}

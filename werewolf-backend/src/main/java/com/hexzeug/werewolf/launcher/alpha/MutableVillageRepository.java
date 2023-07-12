package com.hexzeug.werewolf.launcher.alpha;

import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.VillageRepository;

/**
 * The {@code MutableVillageRepository} interface represents a repository's interface for adding and removing villages.
 */
public interface MutableVillageRepository extends VillageRepository {

    /**
     * Adds a village to the repository.
     *
     * @param village The village to be added.
     */
    void addVillage(Village village);

    /**
     * Removes a village from the repository.
     *
     * @param village The village to be removed.
     */
    void removeVillage(Village village);
}

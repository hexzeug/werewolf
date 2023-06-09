package com.hexszeug.werewolf.launcher.alpha;

import com.hexszeug.werewolf.game.model.village.Village;

/**
 * The {@code MutableVillageRepository} interface represents a repository's interface for adding and removing villages.
 */
public interface MutableVillageRepository {

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

package com.hexszeug.werewolf.game.model.village;

/**
 * The VillageRepository interface represents a repository for accessing villages.
 */
public interface VillageRepository {

    /**
     * Retrieves a village by its ID.
     *
     * @param villageId the ID of the village
     * @return the Village object associated with the specified villageId,
     *         or null if no village is found with the given identifier
     */
    Village getByVillageId(String villageId);
}

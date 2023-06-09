package com.hexszeug.werewolf.game.model.authorization;

import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import com.hexszeug.werewolf.launcher.alpha.MutableAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class AuthorizationRepositoryImpl implements AuthorizationRepository, MutableAuthorizationRepository {
    private final VillageRepository villageRepository;
    private final Map<String, PlayerLocation> authTokenPlayerLocationMap = new HashMap<>();

    @Override
    public Player getPlayerByAuthToken(String authToken) {
        if (authToken == null) return  null;
        PlayerLocation playerLocation = authTokenPlayerLocationMap.get(authToken);
        if (playerLocation == null) return null;
        Village village = villageRepository.getByVillageId(playerLocation.getVillageId());
        if (village == null) return null;
        return village.getByPlayerId(playerLocation.getPlayerId());
    }

    public void addAuthorization(String authToken, String villageId, String playerId) {
        if (authToken == null || villageId == null || playerId == null) return;
        authTokenPlayerLocationMap.put(authToken, new PlayerLocation(villageId, playerId));
    }

    public void removeAuthorization(String authToken) {
        if (authToken == null) return;
        authTokenPlayerLocationMap.remove(authToken);
    }

    @Value
    private static class PlayerLocation {
        String villageId;
        String playerId;
    }
}

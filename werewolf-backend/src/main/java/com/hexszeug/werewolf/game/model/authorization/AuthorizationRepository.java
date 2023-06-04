package com.hexszeug.werewolf.game.model.authorization;

import com.hexszeug.werewolf.game.model.player.Player;

public interface AuthorizationRepository {
    Player getPlayerByAuthToken(String authToken);
}

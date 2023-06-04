package com.hexszeug.werewolf.game.model.player;

import com.hexszeug.werewolf.game.model.customprops.CustomProperties;
import com.hexszeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexszeug.werewolf.game.model.player.role.Role;

public interface Player extends CustomProperties {
    String getPlayerId();
    String getVillageId();
    String getName();
    Role getRole();
    boolean isAlive();
    void setAlive(boolean alive);
    DeathReason getDeathReason();
    void setDeathReason(DeathReason deathReason);
    int getDeathIGTime();
    void setDeathIGTime(int deathIGTime);
}

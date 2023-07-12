package com.hexzeug.werewolf.game.model.player;

import com.hexzeug.werewolf.game.model.player.deathreason.DeathReason;
import com.hexzeug.werewolf.game.model.player.role.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerImpl implements Player {
    @EqualsAndHashCode.Include
    private final String playerId;
    private final String villageId;
    private final String name;
    private final Role role;
    private boolean alive = true;
    private DeathReason deathReason;
    private int deathIGTime = -1;

    @Getter(AccessLevel.NONE)
    private final Map<String, Object> customProps = new HashMap<>();

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
}

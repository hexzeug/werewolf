package com.hexzeug.werewolf.launcher.alpha;

import com.hexzeug.werewolf.game.model.village.Village;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {
    private List<UserIdentity> userIdentities;
    @EqualsAndHashCode.Include
    private final UUID uuid;
    private final Instant created = Instant.now();
    private boolean running;
    private Village village;
}

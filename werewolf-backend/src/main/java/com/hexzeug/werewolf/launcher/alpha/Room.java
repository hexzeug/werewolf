package com.hexzeug.werewolf.launcher.alpha;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Room {
    private List<UserIdentity> userIdentities;
    private final UUID uuid;
    private final Instant created = Instant.now();
    private boolean running;
}

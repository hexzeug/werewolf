package com.hexzeug.werewolf.launcher.alpha;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Base64;
import java.util.Random;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserIdentity {
    @EqualsAndHashCode.Include
    private final String authToken;
    private String name;
    private Room room;

    public UserIdentity(Random random, Room room) {
        this.room = room;
        authToken = generateRandomString(128, random);
    }

    public UserIdentity(Random random, String name) {
        this(random, (Room) null);
        this.name = name;
    }

    public static String generateRandomString(int length, Random random) {
        byte[] randomBytes = new byte[length / 4 * 3];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

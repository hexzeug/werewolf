package com.hexzeug.werewolf.launcher.alpha;

import com.hexzeug.werewolf.game.model.village.Village;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameManager {
    private final GameCreationService gameCreationService;
    private final MutableAuthorizationRepository authorizationRepository;
    private final MutableVillageRepository villageRepository;

    private final Map<String, UserIdentity> authMap = new HashMap<>();
    private final Set<Room> roomCache = new HashSet<>();

    public List<String> newRoom(int playerCount) {
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId);
        roomCache.add(room);

        Random random = new SecureRandom();
        List<UserIdentity> userIdentities = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            UserIdentity userIdentity = new UserIdentity(random, room);
            userIdentities.add(userIdentity);
            authMap.put(userIdentity.getAuthToken(), userIdentity);
        }

        room.setUserIdentities(userIdentities);
        return userIdentities.stream().map(UserIdentity::getAuthToken).toList();
    }

    public boolean isRunning(String authToken) {
        UserIdentity userIdentity = authMap.get(authToken);
        if (userIdentity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userIdentity.getRoom().isRunning();
    }

    public void setName(String authToken, String name) {
        UserIdentity userIdentity = authMap.get(authToken);
        if (userIdentity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Room room = userIdentity.getRoom();
        if (room.isRunning() || userIdentity.getName() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (name == null || name.length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userIdentity.setName(name);
        if (room.getUserIdentities().stream().anyMatch(ui -> ui.getName() == null)) return;
        room.setRunning(true);
        gameCreationService.createGame(room.getUserIdentities());
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void cleanUp() {
        Iterator<Room> iterator = roomCache.iterator();
        while (iterator.hasNext()) {
            Room room = iterator.next();
            long lifetime = Duration.between(room.getCreated(), Instant.now()).toMinutes();
            if (!room.isRunning() && lifetime < 30 || room.isRunning() && lifetime < 60) continue;
            if (room.isRunning()) deleteGame(room);
            room.getUserIdentities().forEach(userIdentity -> authMap.remove(userIdentity.getAuthToken()));
            iterator.remove();
        }
    }

    private void deleteGame(Room room) {
        Village village = villageRepository.getByVillageId(
                authorizationRepository.getPlayerByAuthToken(
                        room.getUserIdentities().get(0).getAuthToken()
                ).getVillageId()
        );
        room.getUserIdentities().forEach(
                userIdentity -> authorizationRepository.removeAuthorization(userIdentity.getAuthToken())
        );
        villageRepository.removeVillage(village);
    }
}

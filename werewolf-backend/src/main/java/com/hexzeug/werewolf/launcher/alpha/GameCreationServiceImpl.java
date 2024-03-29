package com.hexzeug.werewolf.launcher.alpha;

import com.hexzeug.werewolf.game.model.player.Player;
import com.hexzeug.werewolf.game.model.player.PlayerImpl;
import com.hexzeug.werewolf.game.model.player.role.Role;
import com.hexzeug.werewolf.game.model.village.Village;
import com.hexzeug.werewolf.game.model.village.VillageImpl;
import com.hexzeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@RequiredArgsConstructor
@Log4j2
@Service
public class GameCreationServiceImpl implements GameCreationService {
    private static final int PLAYERS_MIN = 2;
    private static final int PLAYERS_MAX = 18;
    private static final List<Role> DEFAULT_ROLES = List.of(
            Role.WEREWOLF,
            Role.WITCH,
            Role.SEER,
            Role.WEREWOLF,
            Role.CUPID,
            Role.HUNTER,
            Role.WEREWOLF,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.WEREWOLF,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER,
            Role.VILLAGER
    );

    private final MutableAuthorizationRepository authorizationRepository;
    private final MutableVillageRepository villageRepository;

    @Override
    public void createGame(int playerCount) {
        if (playerCount < PLAYERS_MIN || playerCount > PLAYERS_MAX) {
            throw new IllegalArgumentException(
                    "player count must lie between %s and %s.".formatted(PLAYERS_MIN, PLAYERS_MAX)
            );
        }

        List<String> names = new ArrayList<>(PLAYER_NAMES);
        Collections.shuffle(names, new Random());

        // always generate the same auth tokens for better debugging
        SecureRandom secureRandom = new SecureRandom(new byte[]{123});

        List<UserIdentity> userIdentities = new ArrayList<>(playerCount);
        log.info("=============");
        log.info(String.format("Printing authentication tokens for %s players:", playerCount));
        log.info("    name: auth token");
        for (int i = 0; i < playerCount; i++) {
            UserIdentity userIdentity = new UserIdentity(secureRandom, names.get(i));
            userIdentities.add(userIdentity);
            log.info("    %s: %s".formatted(userIdentity.getName(), userIdentity.getAuthToken()));
        }
        log.info("=============");

        createGame(userIdentities);
    }
    @Override
    public Village createGame(List<UserIdentity> userIdentities) {
        int playerCount = userIdentities.size();
        if (playerCount < PLAYERS_MIN || playerCount > PLAYERS_MAX) {
            throw new IllegalArgumentException(
                    "player count must lie between %s and %s.".formatted(PLAYERS_MIN, PLAYERS_MAX)
            );
        }

        // roles
        Random random = new Random();
        List<Role> roles = new ArrayList<>(DEFAULT_ROLES.subList(0, playerCount));
        Collections.shuffle(roles, random);

        // phase order
        List<Phase> phaseOrder = generatePhaseOrder(roles);

        // village id
        String villageId;
        do {
            villageId = UserIdentity.generateRandomString(12, random);
        } while (villageRepository.getByVillageId(villageId) != null);

        // players
        List<Player> players = new ArrayList<>(playerCount);
        Set<String> usedPlayerIds = new HashSet<>();
        for (int i = 0; i < playerCount; i++) {
            String playerId;
            do {
                playerId = UserIdentity.generateRandomString(16, random);
            } while (usedPlayerIds.contains(playerId));
            usedPlayerIds.add(playerId);
            players.add(new PlayerImpl(
                    playerId,
                    villageId,
                    userIdentities.get(i).getName(),
                    roles.get(i)
            ));
        }

        // village
        Village village = new VillageImpl(villageId, phaseOrder, players);
        villageRepository.addVillage(village);

        // authorization
        for (int i = 0; i < playerCount; i++) {
            authorizationRepository.addAuthorization(
                    userIdentities.get(i).getAuthToken(),
                    villageId,
                    players.get(i).getPlayerId()
            );
        }

        return village;
    }

    private List<Phase> generatePhaseOrder(List<Role> roles) {
        List<Phase> phases = new ArrayList<>();
        if (roles.contains(Role.CUPID)) phases.add(Phase.CUPID);
        if (roles.contains(Role.SEER)) phases.add(Phase.SEER);
        phases.add(Phase.WEREWOLVES);
        if (roles.contains(Role.WITCH)) {
            phases.add(Phase.WITCH_HEAL);
            phases.add(Phase.WITCH_POISON);
        }
        if (roles.contains(Role.HUNTER)) phases.add(Phase.HUNTER);
        phases.add(Phase.ACCUSATION);
        phases.add(Phase.COURT);
        if (roles.contains(Role.HUNTER)) phases.add(Phase.HUNTER);
        return phases;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> PLAYER_NAMES = List.of(
            "bapi", "beqa", "bewo", "bido", "bize", "bojo", "bomp", "bova", "bram", "brax", "brix", "ceju",
            "cend", "cleg", "coyo", "crob", "cufa", "cupo", "cuya", "dexu", "dorj", "doxo", "dram", "dren", "drez",
            "drip", "dron", "drox", "drup", "dupo", "fage", "fend", "feza", "firk", "fiwo", "fizu", "foku", "fowp",
            "frax", "friz", "froj", "froz", "fuja", "gawe", "glip", "glix", "goka", "grev", "grom", "grum", "gupo",
            "gwen", "hazo", "hefu", "hend", "homp", "hybe", "jalk", "jeld", "jert", "jorb", "jore", "jove", "jowp",
            "jurm", "juxo", "kapi", "klax", "klip", "klow", "kodi", "korp", "krax", "kuca", "kuja", "kuno", "kurl",
            "kuxi", "lebo", "liva", "lopu", "lupa", "mava", "milk", "mirk", "mixa", "moxe", "mudo", "muxa", "muxi",
            "nabe", "naji", "nemo", "nepa", "nift", "nolt", "nuxe", "pako", "pema", "peyo", "pibl", "pima", "pixa",
            "pleb", "plom", "plux", "polt", "poya", "puxa", "qaza", "qexo", "qize", "qosi", "quak", "quax", "quay",
            "quaz", "quik", "quix", "quna", "reja", "riza", "rogu", "roto", "sake", "saxi", "scop", "shox", "skaw",
            "skiz", "skop", "skow", "slux", "sner", "snox", "soma", "soya", "suco", "suge", "swip", "swiz", "swum",
            "tano", "thry", "trix", "tuxe", "tuxi", "twiz", "twor", "vaju", "vapa", "veck", "vekt", "vend", "vent",
            "venz", "vept", "vick", "vigo", "vimp", "vixt", "viza", "vond", "vone", "vorg", "vujo", "vurl", "vuta",
            "vuxo", "waje", "welp", "wifa", "wima", "wimp", "worp", "wovi", "wurn", "wuzu", "xabe", "xade", "xamp",
            "xift", "xina", "xode", "xuth", "yado", "yorb", "yorp", "yozz", "yump", "zema", "zern", "zeya", "zimp",
            "zint", "zoma", "zomp", "zopa", "zovo", "zuxo"
    );
}

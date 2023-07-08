package com.hexszeug.werewolf.launcher.alpha;

import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.player.PlayerImpl;
import com.hexszeug.werewolf.game.model.player.role.Role;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageImpl;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import com.hexszeug.werewolf.game.model.village.phase.Phase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@RequiredArgsConstructor
@Log4j2
@Service
public class GameCreationServiceImpl implements GameCreationService {
    private static final int PLAYERS_MIN = 1;
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
    private final VillageRepository readableVillageRepository;

    @Override
    public void createGame(int playerCount) {
        if (playerCount < PLAYERS_MIN || playerCount > PLAYERS_MAX) {
            throw new IllegalArgumentException(String.format(
                    "playerCount must lie between %s and %s.",
                    PLAYERS_MIN,
                    PLAYERS_MAX
            ));
        }
        List<Role> roles = new ArrayList<>(DEFAULT_ROLES.subList(0, playerCount));
        List<Phase> phaseOrder = generatePhaseOrder(roles);

        Random random = new Random();
        List<String> names = new ArrayList<>(PLAYER_NAMES);
        Collections.shuffle(roles, random);
        Collections.shuffle(names, random);
        String villageId;
        do {
            villageId = generateRandomString(12, random);
        } while (readableVillageRepository.getByVillageId(villageId) != null);
        List<Player> players = new ArrayList<>(playerCount);
        Set<String> usedPlayerIds = new HashSet<>();
        for (int i = 0; i < playerCount; i++) {
            String playerId;
            do {
                playerId = generateRandomString(16, random);
            } while (usedPlayerIds.contains(playerId));
            usedPlayerIds.add(playerId);
            players.add(new PlayerImpl(
                    playerId,
                    villageId,
                    names.get(i),
                    roles.get(i)
            ));
        }
        Village village = new VillageImpl(villageId, phaseOrder, players);
        villageRepository.addVillage(village);
        // always generate the same auth tokens for better debugging
        SecureRandom secureRandom = new SecureRandom(new byte[]{123});
        log.info("=============");
        log.info(String.format("Printing authentication tokens for %s players:", playerCount));
        log.info("    name: auth token");
        for (Player player : players) {
            String authToken = generateRandomString(128, secureRandom);
            authorizationRepository.addAuthorization(authToken, villageId, player.getPlayerId());
            log.info(String.format("    %s: %s", player.getName(), authToken));
        }
        log.info("=============");
    }

    private String generateRandomString(int length, Random random) {
        byte[] randomBytes = new byte[length / 4 * 3];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
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

package com.hexszeug.werewolf.launcher.alpha;

/**
 * The {@code GameCreationService} interface provides methods for creating a game with a specific number of players.
 * It creates player objects and output the information required to connect clients to the players in the log.
 */
public interface GameCreationService {

    /**
     * Creates a game with the specified number of players.
     *
     * @param playerCount The number of players in the game.
     * @throws IllegalArgumentException if the playerCount is less than 1 or greater than 18.
     */
    void createGame(int playerCount);
}

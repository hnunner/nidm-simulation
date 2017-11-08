package nl.uu.socnetid.network_games.games;

/**
 * Interface of a basic network game.
 *
 * @author Hendrik Nunner
 */
public interface NetworkGame {

    /**
     * Simulation of a network game. The game is repeated until none
     * of the players want to change their network connections anymore
     * (stable network), or until the maximum number of rounds is reached.
     */
    void simulateGame();

}

package nl.uu.socnetid.network_games;

import nl.uu.socnetid.network_games.games.NetworkGame;
import nl.uu.socnetid.network_games.games.SimpleNetworkGame;

/**
 * @author Hendrik Nunner
 */
public class Composition {

    /**
     * @param args
     *          runtime arguments
     */
    public static void main(String[] args) {
        NetworkGame game = SimpleNetworkGame.newInstance(20);
        game.simulateGame();
    }

}

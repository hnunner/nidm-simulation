package nl.uu.socnetid.network_games.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.SimplePlayer;

/**
 * Implementation of a simple {@link NetworkGame}.
 *
 * @author Hendrik Nunner
 */
public class SimpleNetworkGame implements NetworkGame {

    /** List of players playing the network game */
    private List<Player> players;

    /**
     * Private constructor.
     */
    private SimpleNetworkGame() {
        // init of players
        this.players = new ArrayList<Player>();
        for (int i = 0; i < 4; i++) {
            this.players.add(SimplePlayer.newInstance());
        }
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player currPlayer = it.next();
        }
    }

    /**
     * Factory method returning a new {@link NetworkGame} instance.
     *
     * @return a new {@link NetworkGame} instance.
     */
    public static NetworkGame newInstance() {
        return new SimpleNetworkGame();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.games.NetworkGame#simulateGame()
     */
    @Override
    public void simulateGame() {

    }

}

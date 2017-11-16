package nl.uu.socnetid.network_games.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;
import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;

/**
 * Implementation of a simple {@link NetworkGame}.
 *
 * @author Hendrik Nunner
 */
public class SimpleNetworkGame implements NetworkGame {

    // maximum rounds for the simulation
    private static final int MAX_ROUNDS = 200;
    // network
    private Network network;

    /**
     * Private constructor.
     */
    private SimpleNetworkGame(int playerCnt) {

        // init utility function
        CumulativeUtilityFunction utilityFunction = new CumulativeUtilityFunction();

        // init players
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < playerCnt; i++) {
            players.add(RationalPlayer.newInstance(utilityFunction));
        }

        // init network
        this.network = new SimpleNetwork(players);
    }

    /**
     * Factory method returning a new {@link NetworkGame} instance.
     *
     * @param playerCnt
     *          the number of players in the game
     * @return a new {@link NetworkGame} instance.
     */
    public static NetworkGame newInstance(int playerCnt) {
        return new SimpleNetworkGame(playerCnt);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.games.NetworkGame#simulateGame()
     */
    @Override
    public void simulateGame() {

        boolean networkStable = false;
        int currentRound = 1;

        while (!networkStable && currentRound < MAX_ROUNDS) {

            List<Player> players = new ArrayList<Player>(network.getPlayers());
            Collections.shuffle(players);
            Iterator<Player> playersIt = players.iterator();

            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();

                //
                boolean connectFirst = ThreadLocalRandom.current().nextBoolean();

                if (connectFirst) {
                    Player newConnection = currPlayer.seekNewConnection();
                    if (newConnection != null) {
                        newConnection.acceptConnection(currPlayer);
                    }

                    Player costlyConnection = currPlayer.seekCostlyConnection();
                } else {

                }


            }



            currentRound += 1;
        }




    }

}

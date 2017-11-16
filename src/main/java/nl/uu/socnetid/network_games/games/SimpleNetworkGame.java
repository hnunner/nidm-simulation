package nl.uu.socnetid.network_games.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.networks.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.networks.writer.AdjacencyMatrixWriter;
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

    // logger
    private static final Logger LOGGER = Logger.getLogger(SimpleNetworkGame.class);


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

        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();
            currPlayer.initCoPlayers(players);
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

            LOGGER.debug("\n"
                    + "##################################################\n"
                    + "Starting simulation round " + currentRound);

            // players performing action in random order
            List<Player> players = new ArrayList<Player>(network.getPlayers());
            Collections.shuffle(players);

            // each player
            Iterator<Player> playersIt = players.iterator();
            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();

                boolean tryToConnectFirst = ThreadLocalRandom.current().nextBoolean();

                // 1st try to connect - 2nd try to disconnect if no new connection desired
                if (tryToConnectFirst) {

                    // try to connect
                    if (!tryToConnect(currPlayer)) {
                        // try to disconnect
                        tryToDisconnect(currPlayer);
                    }

                // 1st try to disconnect - 2nd try to connect if no disconnection desired
                } else {

                    // try to disconnect
                    if (!tryToDisconnect(currPlayer)) {
                        // try to connect
                        tryToConnect(currPlayer);
                    }
                }
            }
            currentRound += 1;
        }

        // write results
        NetworkFileWriter fileWriter = new NetworkFileWriter("network.csv",
                new AdjacencyMatrixWriter(), network);
        fileWriter.write();
    }

    /**
     * @param currPlayer
     */
    private boolean tryToConnect(Player currPlayer) {
        Player potentialNewConnection = currPlayer.seekNewConnection();
        if (potentialNewConnection != null) {
            // other player accepting connection?
            if (potentialNewConnection.acceptConnection(currPlayer)) {
                currPlayer.addConnection(potentialNewConnection);
                potentialNewConnection.addConnection(currPlayer);
            }
        }
        // potential new connection counts as a move
        return (potentialNewConnection != null);
    }

    /**
     * @param currPlayer
     */
    private boolean tryToDisconnect(Player currPlayer) {
        Player costlyConnection = currPlayer.seekCostlyConnection();
        if (costlyConnection != null) {
            currPlayer.removeConnection(costlyConnection);
            costlyConnection.removeConnection(currPlayer);
        }
        return (costlyConnection != null);
    }

}

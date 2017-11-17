package nl.uu.socnetid.network_games.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.networks.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.networks.writer.EdgeListWriter;
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
    private static final int MAX_ROUNDS = 5000;
    // network
    private Network network;
    // graph
    private Graph graph;

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SimpleNetworkGame.class);


    /**
     * Private constructor.
     */
    private SimpleNetworkGame(int playerCnt) {

        // init utility function
        CumulativeUtilityFunction utilityFunction = new CumulativeUtilityFunction();

        // init graphstream
        this.graph = new SingleGraph("NetworkGames");
        this.graph.display();

        // init players
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < playerCnt; i++) {
            Player player = RationalPlayer.newInstance(utilityFunction);
            players.add(player);
            this.graph.addNode(String.valueOf(player.getId()));
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

            // flag whether all players are satisfied with the current network
            boolean allSatisfied = true;

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
                    if (tryToConnect(currPlayer)) {
                        allSatisfied = false;
                    } else {
                        // try to disconnect
                        boolean currSatisfied = !tryToDisconnect(currPlayer);
                        allSatisfied = allSatisfied && currSatisfied;
                    }

                // 1st try to disconnect - 2nd try to connect if no disconnection desired
                } else {

                    // try to disconnect
                    if (tryToDisconnect(currPlayer)) {
                        allSatisfied = false;
                    } else {
                        // try to connect
                        boolean currSatisfied = !tryToConnect(currPlayer);
                        allSatisfied = allSatisfied && currSatisfied;
                    }
                }
            }
            networkStable = allSatisfied;
            currentRound += 1;
        }

        // write results
        NetworkFileWriter fileWriter = new NetworkFileWriter("network.csv",
                new EdgeListWriter(), network);
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
                addEdge(currPlayer, potentialNewConnection);
            }
        }

        // the desire to create new connection counts as a move
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
            removeEdge(currPlayer, costlyConnection);
        }

        // the desire to remove a connection counts as a move
        return (costlyConnection != null);
    }


    /**
     * Adds a graph edge between players 1 and 2.
     *
     * @param player1
     *          first player to create an edge for
     * @param player2
     *          second player to create an edge for
     */
    private void addEdge(Player player1, Player player2) {
        // edge id consistency
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);
        Collections.sort(players);

        String edgeId = String.valueOf(players.get(0).getId()) + String.valueOf(players.get(1).getId());
        String nodeId1 = String.valueOf(players.get(0).getId());
        String nodeId2 = String.valueOf(players.get(1).getId());

        this.graph.addEdge(edgeId, nodeId1, nodeId2);
    }

    /**
     * Removes the graph edge between players 1 and 2.
     *
     * @param player1
     *          first player to remove the edge between
     * @param player2
     *          second player to remove the edge between
     */
    private void removeEdge(Player player1, Player player2) {
        // edge id consistency
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);
        Collections.sort(players);

        String edgeId = String.valueOf(players.get(0).getId()) + String.valueOf(players.get(1).getId());

        this.graph.removeEdge(edgeId);
    }

}

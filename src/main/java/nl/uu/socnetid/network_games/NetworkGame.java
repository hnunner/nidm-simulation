package nl.uu.socnetid.network_games;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.networks.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.networks.writer.EdgeListWriter;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;
import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame {

    // maximum rounds for the simulation
    private static final int MAX_ROUNDS = 5000;
    // init utility function
    private static CumulativeUtilityFunction UTILITY_FUNCTION = new CumulativeUtilityFunction();

    // network
    private Network network;
    // players
    private List<Player> players = new ArrayList<Player>();
    // graph
    private Graph graph;

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // swing frame
    private JFrame frame;

    /**
     * Launch the application.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    NetworkGame window = new NetworkGame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public NetworkGame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // init swing frame
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateGame();
            }
        });
        btnStart.setBounds(53, 208, 142, 45);
        frame.getContentPane().add(btnStart);

        JButton btnAddPlayer = new JButton("Add Player");
        btnAddPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });
        btnAddPlayer.setBounds(53, 22, 142, 29);
        frame.getContentPane().add(btnAddPlayer);

        JButton btnRemovePlayer = new JButton("Remove Player");
        btnRemovePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePlayer();
            }
        });
        btnRemovePlayer.setBounds(53, 63, 142, 29);
        frame.getContentPane().add(btnRemovePlayer);

        JButton btnNewButton = new JButton("Clear Edges");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnNewButton.setBounds(53, 128, 142, 29);
        frame.getContentPane().add(btnNewButton);


        // init graphstream
        this.graph = new SingleGraph("NetworkGames");
        this.graph.display();
    }


    /**
     * Adds a player to the game.
     */
    private void addPlayer() {
        Player player = RationalPlayer.newInstance(UTILITY_FUNCTION);
        players.add(player);
        this.graph.addNode(String.valueOf(player.getId()));
    }

    /**
     * Removes a player from the game.
     */
    private void removePlayer() {
        if (players.size() == 0) {
            return;
        }
        Player player = players.get(players.size() - 1);
        players.remove(player);
        this.graph.removeNode(String.valueOf(player.getId()));
    }

    /**
     * Clears all edges from the graph.
     */
    private void clearEdges() {
        for(Node node:this.graph) {
            Edge[] edges = node.getEdgeSet().toArray(new Edge[0]);
            for(int i = 0; i < edges.length; ++i){
                graph.removeEdge(edges[i]);
            }
        }
        this.network.clearConnections();
    }


    /**
     * Runs the actual simulation of the network game.
     */
    public void simulateGame() {

        // init Players
        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();
            currPlayer.initCoPlayers(players);
        }
        // init network
        this.network = new SimpleNetwork(players);


        boolean networkStable = false;
        int currentRound = 1;

        while (!networkStable && currentRound < MAX_ROUNDS) {

            // flag whether all players are satisfied with the current network
            boolean allSatisfied = true;

            // players performing action in random order
            List<Player> players = new ArrayList<Player>(network.getPlayers());
            Collections.shuffle(players);

            // each player
            playersIt = players.iterator();
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

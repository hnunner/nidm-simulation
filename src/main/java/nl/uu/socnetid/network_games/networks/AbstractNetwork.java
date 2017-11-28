package nl.uu.socnetid.network_games.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.GenericDisease;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // maximum rounds for the simulation
    private static final int MAX_ROUNDS = 5000;

    // set of players
    private List<Player> players;

    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     */
    protected AbstractNetwork(List<Player> players) {
        this.players = players;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#addPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void addPlayer(Player player) {
        this.players.add(player);

        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#removePlayer()
     */
    @Override
    public void removePlayer() {
        if (this.players.size() == 0) {
            return;
        }
        Player player = this.players.get(this.players.size() - 1);
        player.destroy();
        this.players.remove(player);

        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#simulate()
     */
    @Override
    public void simulate() {
        simulate(MAX_ROUNDS, 0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#simulate(int, int)
     */
    @Override
    public void simulate(int maxRounds, int delay) {

        // for !RE!-starting the simulation
        clearConnections();

        // initializations
        boolean networkStable = false;
        int currentRound = 1;

        // loop while network is not stable and maximum simulation rounds not yet reached
        while (!networkStable && currentRound < maxRounds) {

            ////////// DISEASE DYNAMICS //////////
            computeDiseaseDynamics();

            ////////// PLAYER DYNAMICS //////////
            // flag whether all players are satisfied with the current network (
            boolean allSatisfied = true;

            // players performing action in random order
            Collections.shuffle(this.players);

            // each player
            Iterator<Player> playersIt = players.iterator();
            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();

                // some delay before each player moves (e.g., for animation processes)
                try {
                    Thread.sleep(delay * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // players try to connect or disconnect first in random order
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
    }

    /**
     * A player tries to connect. That means she first seeks a connection that gives higher
     * utility as the current utility; and then requests the corresponding player to establish
     * the new connection.
     *
     * @param player
     *          the player trying to find a new valuable connection
     */
    private boolean tryToConnect(Player player) {
        Player potentialNewConnection = player.seekNewConnection();
        if (potentialNewConnection != null) {
            // other player accepting connection?
            if (potentialNewConnection.acceptConnection(player)) {
                player.addConnection(potentialNewConnection);
                potentialNewConnection.addConnection(player);
            }
        }
        // the desire to create new connection counts as a move
        return (potentialNewConnection != null);
    }

    /**
     * A player tries to disconnect. That means she seeks a connection that creates more costs
     * than benefits. In case she finds such a connection, she removes the costly connection.
     *
     * @param player
     *          the player trying to remove a costly connection
     */
    private boolean tryToDisconnect(Player player) {
        Player costlyConnection = player.seekCostlyConnection();
        if (costlyConnection != null) {
            player.removeConnection(costlyConnection);
            costlyConnection.removeConnection(player);
        }

        // the desire to remove a connection counts as a move
        return (costlyConnection != null);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getPlayers()
     */
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getConnectionsForPlayer(nl.uu.socnetid.network_games.
     * players.Player)
     */
    @Override
    public List<Player> getConnectionsOfPlayer(Player player) {
        return player.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().removeAllConnections();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#initUtilityFunction(nl.uu.socnetid.network_games.utility_functions.UtilityFunction)
     */
    @Override
    public void initUtilityFunction(UtilityFunction utilityFunction) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().setUtilityFunction(utilityFunction);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#infectRandomPlayer()
     */
    @Override
    public void infectRandomPlayer() {
        if (this.players == null || this.players.isEmpty()) {
            return;
        }

        // players performing action in random order
        Collections.shuffle(this.players);

        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();

            if (player.isInfected()) {
                continue;
            }

            GenericDisease disease = new GenericDisease();
            player.infect(disease);
            logger.debug("Player " + player.getId() + " successfully infected with " + disease.getClass());
            return;
        }
        logger.debug("No player infected. All players infected already.");
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#computeDiseaseDynamics()
     */
    @Override
    public void computeDiseaseDynamics() {
        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();

            if (currPlayer.isInfected()) {
                currPlayer.fightDisease();

                if (currPlayer.isInfectious()) {
                    currPlayer.computeTransmissions();
                }
            }
        }

        Collections.sort(this.players);
        StringBuilder sb = new StringBuilder("###############################\n");
        playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();
            sb.append("player ").append(currPlayer.getId());

            if (currPlayer.isInfected()) {
                sb.append(":     infected.\n");
            } else {
                sb.append(": not infected.\n");
            }
        }

        logger.debug(sb.toString());
    }

}

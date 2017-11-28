package nl.uu.socnetid.network_games.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.GenericDisease;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // logger
    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // maximum rounds for the simulation
    private static final int MAX_ROUNDS = 5000;

    // set of players
    private List<Player> players;

    // concurrency
    ExecutorService service;
    final ReentrantLock lock = new ReentrantLock();


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

        service = Executors.newFixedThreadPool(this.players.size());

        // for !RE!-starting the simulation
        clearConnections();

        // initializations
        boolean networkStable = false;
        int currentRound = 0;

        // loop while network is not stable and maximum simulation rounds not yet reached
        while (!networkStable && currentRound++ < maxRounds) {

            // disease dynamics
            computeDiseaseDynamics();

            // player dynamics
            // players performing action in random order
            Collections.shuffle(this.players);

            // each player
            Iterator<Player> playersIt = players.iterator();
            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();
                currPlayer.setLock(this.lock);
                service.submit(currPlayer);
            }

            playersIt = players.iterator();
            boolean allSatisfied = true;
            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();
                allSatisfied &= currPlayer.isSatisfied();
            }
            networkStable = allSatisfied;
        }

        if (networkStable) {
            logger.debug("Network stable after " + currentRound + " rounds.");
        }
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
     * @see nl.uu.socnetid.network_games.networks.Network#initUtilityFunction(nl.uu.socnetid.network_games.
     * utility_functions.UtilityFunction)
     */
    @Override
    public void initUtilityFunction(UtilityFunction utilityFunction) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().setUtilityFunction(utilityFunction);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#initDelay(int)
     */
    @Override
    public void initSimulationDelay(int delay) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().setDelay(delay);
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
            return;
        }
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
    }

}

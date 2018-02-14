package nl.uu.socnetid.network_games.network.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.stats.GlobalActorStats;
import nl.uu.socnetid.network_games.stats.GlobalNetworkStats;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // logger
    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // set of players
    private List<Player> players;

    // stats
    private GlobalActorStats actorStats = new GlobalActorStats();
    private double cumulatedRisk = 0.0;

    // listener
    private final Set<NetworkStabilityListener> listeners = new CopyOnWriteArraySet<NetworkStabilityListener>();


    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     */
    protected AbstractNetwork(List<Player> players) {
        this.players = players;

        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            updateActorStats(player, true);
        }
    }

    /**
     * Updates the actor stats.
     *
     * @param player
     *          the player with the information to be updated
     * @param add
     *          true if a player is being added, false if a player is being removed
     */
    private void updateActorStats(Player player, boolean add) {

        if (add) {
            this.actorStats.incActorsOverall();
            this.cumulatedRisk += player.getRiskFactor();
        } else {
            this.actorStats.decActorsOverall();
            this.cumulatedRisk -= player.getRiskFactor();
        }

        // risk
        this.actorStats.setAvRisk(this.players.size() > 0 ?
                this.cumulatedRisk / this.players.size() :
                    0.0);

        if (player.getRiskFactor() == 1.0) {
            if (add) {
                this.actorStats.incRiskNeutrals();
            } else {
                this.actorStats.decRiskNeutrals();
            }

        } else if (player.getRiskFactor() > 1.0) {
            if (add) {
                this.actorStats.incRiskAverse();
            } else {
                this.actorStats.decRiskAverse();
            }

        } else if (player.getRiskFactor() < 1.0) {
            if (add) {
                this.actorStats.incRiskSeeking();
            } else {
                this.actorStats.decRiskSeeking();
            }

        } else {
            logger.warn("Undefined risk behavior for player " + player.getId()
            + ": " + player.getRiskFactor());
        }

        // disease
        switch (player.getDiseaseGroup()) {
            case SUSCEPTIBLE:
                if (add) {
                    this.actorStats.incSusceptibles();
                } else {
                    this.actorStats.decSusceptibles();
                }
                break;

            case INFECTED:
                if (add) {
                    this.actorStats.incInfected();
                } else {
                    this.actorStats.decInfected();
                }
                break;

            case RECOVERED:
                if (add) {
                    this.actorStats.incRecovered();
                } else {
                    this.actorStats.decRecovered();
                }
                break;

            default:
                logger.warn("Unknown disease group for player " + player.getId()
                        + ": " + player.getDiseaseGroup());
        }
    }

    /**
     * Resets the actor stats to all actors being susceptible.
     */
    private void resetActorStats() {
        this.actorStats.setSusceptibles(this.players.size());
        this.actorStats.setInfected(0);
        this.actorStats.setRecovered(0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#addPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void addPlayer(Player player) {
        this.players.add(player);

        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }

        // update stats
        updateActorStats(player, true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#removePlayer()
     */
    @Override
    public void removePlayer() {
        if (this.players.size() == 0) {
            return;
        }
        Player player = this.players.get(this.players.size() - 1);

        // remove
        player.destroy();
        this.players.remove(player);


        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }

        // update stats
        updateActorStats(player, false);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#getPlayers()
     */
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#getPlayer(long)
     */
    @Override
    public Player getPlayer(long id) {
        // dirty but okay for now
        Iterator<Player> it = this.players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getId() == id) {
                return player;
            }
        }

        logger.warn("No player found for ID " + id);
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#getConnectionsForPlayer(nl.uu.socnetid.network_games.
     * players.Player)
     */
    @Override
    public List<Player> getConnectionsOfPlayer(Player player) {
        return player.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().removeAllConnections();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#resetPlayers()
     */
    @Override
    public void resetPlayers() {
        clearConnections();
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            player.makeSusceptible();

            // reset actor stats
            resetActorStats();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#infectRandomPlayer(
     * nl.uu.socnetid.network_games.disease.DiseaseSpecs)
     */
    @Override
    public void infectRandomPlayer(DiseaseSpecs diseaseSpecs) {
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

            // update stats
            if (player.isSusceptible()) {
                this.actorStats.decSusceptibles();
            } else if (player.isRecovered()) {
                this.actorStats.decRecovered();
            } else {
                logger.warn("Unable to (force-) infect an actor that is either susceptible, nor recovered");
                return;
            }
            player.forceInfect(diseaseSpecs);
            this.actorStats.incInfected();

            return;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#toggleInfection(long,
     * nl.uu.socnetid.network_games.disease.DiseaseSpecs)
     */
    @Override
    public void toggleInfection(long playerId, DiseaseSpecs diseaseSpecs) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            if (player.getId() == playerId) {

                switch (player.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        player.forceInfect(diseaseSpecs);
                        this.actorStats.decSusceptibles();
                        this.actorStats.incInfected();
                        break;

                    case INFECTED:
                        player.cure();
                        this.actorStats.decInfected();
                        this.actorStats.incRecovered();
                        break;

                    case RECOVERED:
                        player.makeSusceptible();
                        this.actorStats.decRecovered();
                        this.actorStats.incSusceptibles();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#getGlobalActorStats()
     */
    @Override
    public GlobalActorStats getGlobalActorStats() {
        return this.actorStats;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#getGlobalNetworkStats()
     */
    @Override
    public GlobalNetworkStats getGlobalNetworkStats() {

        boolean stable = true;
        int connections = 0;
        double avDegree = 0.0;

        // TODO implement diameter and average distance (if it adds to understanding)
        int diameter = 0;
        double avDistance = 0.0;

        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            stable &= player.isSatisfied();
            connections += player.getConnections().size();
        }
        avDegree = this.players.size() == 0 ? 0.0 : (double) connections / this.players.size();
        connections /= 2;

        return new GlobalNetworkStats(stable, connections, avDegree, diameter, avDistance);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#addListener(
     * nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener)
     */
    @Override
    public void addListener(NetworkStabilityListener listener) {
        this.listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#removeListener(
     * nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener)
     */
    @Override
    public void removeListener(NetworkStabilityListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners of task completion.
     */
    private final void notifyListeners() {
        Iterator<NetworkStabilityListener> listenersIt = this.listeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notify(this);
        }
    }

}

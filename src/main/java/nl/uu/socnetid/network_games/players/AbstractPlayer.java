package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.Disease;
import nl.uu.socnetid.network_games.disease.InfectionState;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * Shared fields and methods for all {@link Player} types.
 *
 * @author Hendrik Nunner
 */
public abstract class AbstractPlayer implements Player {

    // logger
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(AbstractPlayer.class);

    // unique identifier
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    // utility function
    public UtilityFunction utilityFunction;

    // co-players
    private List<Player> coPlayers;
    // personal connections
    private List<Player> connections = new ArrayList<Player>();

    // disease
    private InfectionState infectionState;
    private Disease disease;


    /**
     * Constructor.
     */
    protected AbstractPlayer() {
        this.infectionState = InfectionState.SUSCEPTIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#initCoPlayers()
     */
    @Override
    public void initCoPlayers(List<Player> allPlayers) {
        coPlayers = new ArrayList<Player>(allPlayers);
        coPlayers.remove(this);
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Player p) {
        return (int) (this.getId() - p.getId());
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getCurrentUtility()
     */
    @Override
    public double getUtility() {
        return getUtility(this.connections);
    }

    /**
     * Gets the utility for a player based on a list of connections.
     *
     * @param connections
     *          the connections to compute the utility for
     * @return the utility for a player based on a list of connections
     */
    protected double getUtility(List<Player> connections) {
        return utilityFunction.getUtility(this, connections);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#setUtilityFunction(nl.uu.socnetid.network_games.utility_functions.UtilityFunction)
     */
    @Override
    public void setUtilityFunction(UtilityFunction utilityFunction) {
        this.utilityFunction = utilityFunction;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getConnections()
     */
    @Override
    public List<Player> getConnections() {
        return connections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getRandomConnection()
     */
    @Override
    public Player getRandomConnection() {
        if (connections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return connections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getRandomNotYetConnectedPlayer()
     */
    @Override
    public Player getRandomNotYetConnectedPlayer() {
        ArrayList<Player> noConnections = new ArrayList<Player>(coPlayers);
        noConnections.removeAll(connections);

        if (noConnections.size() == 0) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(noConnections.size());
        return noConnections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#addConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean addConnection(Player newConnection) {
        if (newConnection.equals(this)) {
            throw new RuntimeException("Unable to create reflexive connections.");
        }
        if (connections.contains(newConnection)) {
            throw new RuntimeException("Unable to create more than one connection to the same player.");
        }
        return this.connections.add(newConnection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean removeConnection(Player connection) {
        if (connection.equals(this)) {
            throw new RuntimeException("Unable to remove reflexive connections.");
        }
        return this.connections.remove(connection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {
        this.connections = new ArrayList<Player>();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#destroy()
     */
    @Override
    public void destroy() { }




    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getInfectionState()
     */
    @Override
    public InfectionState getInfectionState() {
        return this.infectionState;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#infect(nl.uu.socnetid.network_games.disease.Disease)
     */
    @Override
    public void infect(Disease disease) {
        this.disease = disease;
        this.infectionState = InfectionState.INFECTED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#computeTransmissions()
     */
    @Override
    public void computeTransmissions() {

        Iterator<Player> connectionsIt = this.connections.iterator();
        while (connectionsIt.hasNext()) {
            Player currConnection = connectionsIt.next();

            if (!currConnection.isInfected() && !currConnection.isImmune()
                    && this.disease.isTransmitted()) {
                try {
                    Disease newDiseaseInstance = this.disease.getClass().newInstance();
                    currConnection.infect(newDiseaseInstance);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isInfected()
     */
    @Override
    public boolean isInfected() {
        return this.disease != null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isImmune()
     */
    @Override
    public boolean isImmune() {
        return false;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        if (this.disease == null) {
            return false;
        }
        return this.disease.isInfectious();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#hasSymptoms()
     */
    @Override
    public boolean hasSymptoms() {
        if (this.disease == null) {
            return false;
        }
        return this.disease.isVisible();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#fightDisease()
     */
    @Override
    public void fightDisease() {
        if (this.disease == null) {
            return;
        }

        this.disease.evolve();
        if (this.disease.isDefeated()) {
            this.disease = null;
            this.infectionState = InfectionState.RECOVERED;
        }
    }

}

package nl.uu.socnetid.networkgames.actors;

import java.util.List;
import java.util.concurrent.locks.Lock;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseGroup;
import nl.uu.socnetid.networkgames.utilities.Utility;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Interface of a basic actor.
 *
 * @author Hendrik Nunner
 */
public interface Actor extends Comparable<Actor>, Runnable {

    /**
     * Initializes the actor's list of co-actors.
     *
     * @param actors
     *          all actor's within the game
     */
    void initCoActors(List<Actor> actors);

    /**
     * Getter for the actor's unique identifier.
     *
     * @return the actor's unique identifier
     */
    long getId();

    /**
     * Gets the disease characteristics the actor requires to compute the utility.
     *
     * @return the disease characteristics
     */
    DiseaseSpecs getDiseaseSpecs();

    /**
     * Getter for the actor's current utility.
     *
     * @return the actor's current utility
     */
    Utility getUtility();

    /**
     * Gets the actor's risk factor (r<1: risk seeking; r=1: risk neutral; r>1: risk averse).
     *
     * @return the actor's risk factor
     */
    double getRiskFactor();

    /**
     * Check whether a actor is satisfied with the current connections.
     *
     * @return true if no connections want to be added or removed, false otherwise
     */
    boolean isSatisfied();

    /**
     * Gets the actor's function to compute utilities.
     *
     * @return the actor's function to compute utilities
     */
    public UtilityFunction getUtilityFunction();

    /**
     * Sets the lock required to synchronize threaded actors.
     *
     * @param lock
     *          the lock used to synchronize threaded actors.
     */
    void setLock(Lock lock);

    /**
     * Gets the connections of the actor.
     *
     * @return the connections of the actor
     */
    List<Actor> getConnections();

    /**
     * Gets all co-actors (connections and non-connections).
     *
     * @return the actor's co-actors
     */
    List<Actor> getCoActors();

    /**
     * Gets a random connection of the actor.
     *
     * @return a random connection of the actor.
     */
    Actor getRandomConnection();

    /**
     * Gets a co-actor that is not yet connected to the actor herself.
     *
     * @return a co-actor that is not yet connected to the actor herself
     */
    Actor getRandomNotYetConnectedActor();

    /**
     * Adds a new connection between the actor and another actor.
     *
     * @param newConnection
     *          the actor to create the new connection to
     * @return true if connection is created, false otherwise
     */
    boolean addConnection(Actor newConnection);

    /**
     * Removes a connection between the actor and another actor.
     *
     * @param connection
     *          the other actor to remove the connection to
     * @return true if connection is removed, false otherwise
     */
    boolean removeConnection(Actor connection);

    /**
     * Removes all connections of the actor.
     */
    void removeAllConnections();

    /**
     * Checks whether a new connection creates higher utility. If so,
     * the actor for a new desired connection is returned, null otherwise.
     *
     * @return the actor to create a new connection to, or null in case no
     * new connection is ought to be created
     */
    Actor seekNewConnection();

    /**
     * Checks whether the removal of an existing connection creates higher
     * utility. If so, the actor to break the connection is returned,
     * Null otherwise.
     *
     * @return the actor to break the connection, or null in case no new
     * connection is ought to be broken
     */
    Actor seekCostlyConnection();

	/**
	 * Request to accept a new connection.
	 *
	 * @param newConnection
	 *         the actor requesting the new connection
	 * @return true if the connection is accepted, false otherwise
	 */
	boolean acceptConnection(Actor newConnection);

	/**
	 * Clean up routine when actor is removed from the game.
	 */
	void destroy();

	/**
	 * Infects a actor with a disease.
	 *
	 * @param diseaseSpecs
	 *         the disease a actor gets infected with
	 */
	void infect(DiseaseSpecs diseaseSpecs);

    /**
     * Infects a actor with a disease, no matter his disease state.
     *
     * @param diseaseSpecs
     *         the disease a actor gets infected with
     */
    void forceInfect(DiseaseSpecs diseaseSpecs);

    /**
     * Cures a actor from a disease.
     */
    void cure();

	/**
	 * Computes the transmissions of the disease between the actor and
	 * all of her non-infected connections.
	 */
	void computeTransmissions();

	/**
	 * Gets the disease group (SIR) the actor is currently in.
	 *
	 * @return the disease group (SIR) the actor is currently in
	 */
	DiseaseGroup getDiseaseGroup();

    /**
     * Checks whether a actor is susceptible for a disease.
     *
     * @return true if actor is susceptible, false otherwise
     */
    boolean isSusceptible();

    /**
     * Puts the actor into the susceptible group.
     */
    void makeSusceptible();

    /**
     * Checks whether a actor is infected.
     *
     * @return true if actor is infected, false otherwise
     */
    boolean isInfected();

    /**
     * Checks whether a actor has recovered from a disease.
     *
     * @return true if actor has recovered, false otherwise
     */
    boolean isRecovered();

    /**
     * Triggers the actor to fight the disease.
     */
    void fightDisease();

    /**
     * @return if the actor is infected the function returns the amount of rounds until the actor has recovered,
     *          0 otherwise
     */
    int getTimeUntilRecovered();

    /**
     * Adds a listener to be notified when the actor performed an action.
     *
     * @param actionPerfomedListener
     *          the listener to be notified when the actor has performed an action
     */
    void addActionPerformedListener(final ActionPerformedListener actionPerfomedListener);

    /**
     * Removes a listener.
     *
     * @param actionPerformedListener
     *          the listener to be removed
     */
    void removeActionPerformedListener(final ActionPerformedListener actionPerformedListener);

    /**
     * Adds a listener to be notified when the actor's disease has changed.
     *
     * @param diseaseChangeListener
     *          the listener to be notified when the actor's disease has changed
     */
    void addDiseaseChangeListener(final DiseaseChangeListener diseaseChangeListener);

    /**
     * Removes a listener.
     *
     * @param diseaseChangeListener
     *          the listener to be removed
     */
    void removeDiseaseChangeListener(final DiseaseChangeListener diseaseChangeListener);

}

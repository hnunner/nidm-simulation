package nl.uu.socnetid.networkgames.network.networks;

import java.util.List;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;

/**
 * @author Hendrik Nunner
 */
public interface Network {

    /**
     * Adds a actor to the network.
     *
     * @param actor
     *          the actor to be added
     */
    void addActor(Actor actor);

    /**
     * Removes a actor from the network.
     */
    void removeActor();

    /**
     * Gets all actors within the network.
     *
     * @return all actors within the network.
     */
    List<Actor> getActors();

    /**
     * Get the actor with the corresponding identifier.
     *
     * @param id
     *          the identifier of the actor to get
     * @return the actor with the corresponding identifier
     */
    Actor getActor(long id);

    /**
     * Gets all connections of a specific actor within the network.
     *
     * @param actor
     *          the actor to get the connections for
     * @return all network connections of the specified actor
     */
    List<Actor> getConnectionsOfActor(Actor actor);

    /**
     * Clears all connections between the actors.
     */
    void clearConnections();

    /**
     * Creates the full network based on the actors available.
     */
    void createFullNetwork();

    /**
     * Removes all connections between actors and resets the actors to being susceptible.
     */
    void resetActors();

    /**
     * Infects a random actor.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease to infect a actor with
     */
    void infectRandomActor(DiseaseSpecs diseaseSpecs);

    /**
     * Toggles the infection of a specific actor.
     *
     * @param actorId
     *          the actor's id
     * @param diseaseSpecs
     *          the characteristics of the disease to infect the actor with
     */
    void toggleInfection(long actorId, DiseaseSpecs diseaseSpecs);

}

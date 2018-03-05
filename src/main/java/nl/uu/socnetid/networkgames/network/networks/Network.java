package nl.uu.socnetid.networkgames.network.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorFactory;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class Network extends SingleGraph {

    // logger
    private static final Logger logger = Logger.getLogger(Network.class);

    // counter for unique identifiers
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    // risk factor for risk neutral actors
    private static final double RISK_FACTOR_NEUTRAL = 1.0;

    // listener
    private final Set<ActorAmountListener> actorAmountListeners =
            new CopyOnWriteArraySet<ActorAmountListener>();


    /**
     * Constructor.
     */
    public Network() {
        this("Networks of the Infectious Kind");
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     */
    public Network(String id) {
        super(id);
        this.setNodeFactory(new ActorFactory());
    }


    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return this.addActor(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL);
    }

    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param riskFactor
     *          the factor describing how the actor perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        Actor actor = this.addNode(String.valueOf(NEXT_ID.incrementAndGet()));
        actor.initActor(utilityFunction, diseaseSpecs, riskFactor);
        notifyActorAdded(actor.getId());
        return actor;
    }


    /**
     * Removes a actor from the network.
     */
    public void removeActor() {
        if (this.getActors().size() == 0) {
            return;
        }
        String actorId = this.getLastActor().getId();
        this.removeNode(String.valueOf(actorId));
        notifyActorRemoved(actorId);
    }

    /**
     * Creates the full network based on the actors available.
     */
    public void createFullNetwork() {
        if (this.getActors().size() == 0) {
            return;
        }

        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.connectToAll();
        }
    }

    /**
     * Gets the actor with the corresponding identifier.
     *
     * @param id
     *          the identifier of the actor to get
     * @return the actor with the corresponding identifier
     */
    public Actor getActor(String id) {
        return (Actor) this.getNode(id);
    }

    /**
     * Gets a random actor.
     *
     * @return a random actor
     */
    public Actor getRandomActor() {
        // TODO test if reliable
        int randomIndex = ThreadLocalRandom.current().nextInt(0, getActors().size());
        return (Actor) this.getNode(randomIndex);
    }

    /**
     * Gets the actor with the highest index.
     *
     * @return the actor with the highest index
     */
    public Actor getLastActor() {
        return this.getNode(this.getNodeSet().size() - 1);
    }

    /**
     * Gets all actors within the network.
     *
     * @return all actors within the network.
     */
    public Collection<Actor> getActors() {
        return this.getNodeSet();
    }

    /**
     * Gets an iterator over all actors in an undefined order.
     *
     * @return an iterator over all actors in an undefined order
     */
    public Iterator<Actor> getActorIterator() {
        return this.getNodeIterator();
    }

    /**
     * Clears all connections between the actors.
     */
    public void clearConnections() {
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().removeAllConnections();
        }
    }

    /**
     * Removes all connections between actors and resets the actors to being susceptible.
     */
    public void resetActors() {
        clearConnections();
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.makeSusceptible();
        }
    }

    /**
     * Infects a random actor.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease to infect a actor with
     */
    public void infectRandomActor(DiseaseSpecs diseaseSpecs) {
        if (this.getActors() == null || this.getActors().isEmpty()) {
            return;
        }

        // actors performing action in random order
        List<Actor> actors = new ArrayList<Actor>(this.getActors());
        Collections.shuffle(actors);
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.isInfected()) {
                continue;
            }

            if (actor.isInfected()) {
                logger.warn("Unable to (force-) infect an actor that is already infected");
                return;
            }
            actor.forceInfect(diseaseSpecs);

            return;
        }
    }

    /**
     * Toggles the infection of a specific actor.
     *
     * @param actorId
     *          the actor's id
     * @param diseaseSpecs
     *          the characteristics of the disease to infect the actor with
     */
    public void toggleInfection(String actorId, DiseaseSpecs diseaseSpecs) {
        Iterator<Actor> actorsIt = this.getActorIterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.getId() == actorId) {

                switch (actor.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        actor.forceInfect(diseaseSpecs);
                        break;

                    case INFECTED:
                        actor.cure();
                        break;

                    case RECOVERED:
                        actor.makeSusceptible();
                        break;

                    default:
                        break;
                }
            }
        }
    }


    /**
     * Adds a listener to be notified when the amount of actors is being changed.
     *
     * @param actorAmountListener
     *          the listener to be added
     */
    public void addActorAmountListener(ActorAmountListener actorAmountListener) {
        this.actorAmountListeners.add(actorAmountListener);
    }

    /**
     * Removes a listener that is to be notified when the amount of actors is being changed.
     *
     * @param actorAmountListener
     *          the listener to be removed
     */
    public void removeActorAmountListener(ActorAmountListener actorAmountListener) {
        this.actorAmountListeners.remove(actorAmountListener);
    }

    /**
     * Notifies the listeners of the added actor.
     *
     * @param actorId
     *          the id of the actor being added
     */
    private final void notifyActorAdded(String actorId) {
        Iterator<ActorAmountListener> listenersIt = this.actorAmountListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActorAdded(actorId);
        }
    }

    /**
     * Notifies the actorRemovedListeners of the added actor.
     *
     * @param actorId
     *          the id of the actor being removed
     */
    private final void notifyActorRemoved(String actorId) {
        Iterator<ActorAmountListener> listenersIt = this.actorAmountListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActorRemoved(actorId);
        }
    }

}

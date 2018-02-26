package nl.uu.socnetid.networkgames.network.networks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener;

/**
 * @author Hendrik Nunner
 */
public class Network {

    // logger
    private static final Logger logger = Logger.getLogger(Network.class);

    // list of actors
    private List<Actor> actors;

    // listener
    private final Set<ActorAmountListener> actorAmountListeners =
            new CopyOnWriteArraySet<ActorAmountListener>();


    /**
     * Constructor.
     *
     * @param actors
     *          list of actors in the network
     */
    public Network(List<Actor> actors) {
        this.actors = actors;
    }

    /**
     * Constructor.
     */
    public Network() {
        this(new ArrayList<Actor>());
    }


    /**
     * Adds a actor to the network.
     *
     * @param actor
     *          the actor to be added
     */
    public void addActor(Actor actor) {
        this.actors.add(actor);

        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }

        // notify listeners
        notifyActorAdded(actor.getId());
    }

    /**
     * Removes a actor from the network.
     */
    public void removeActor() {
        if (this.actors.size() == 0) {
            return;
        }
        Actor actor = this.actors.get(this.actors.size() - 1);
        long actorId = actor.getId();

        // remove
        actor.destroy();
        this.actors.remove(actor);

        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }

        // notify listeners
        notifyActorRemoved(actorId);
    }

    /**
     * Creates the full network based on the actors available.
     */
    public void createFullNetwork() {
        if (this.actors.size() == 0) {
            return;
        }

        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.connectToAll();
        }
    }

    /**
     * Gets all actors within the network.
     *
     * @return all actors within the network.
     */
    public List<Actor> getActors() {
        return this.actors;
    }

    /**
     * Get the actor with the corresponding identifier.
     *
     * @param id
     *          the identifier of the actor to get
     * @return the actor with the corresponding identifier
     */
    public Actor getActor(long id) {
        // dirty but okay for now
        Iterator<Actor> it = this.actors.iterator();
        while (it.hasNext()) {
            Actor actor = it.next();
            if (actor.getId() == id) {
                return actor;
            }
        }
        return null;
    }

    /**
     * Clears all connections between the actors.
     */
    public void clearConnections() {
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().removeAllConnections();
        }
    }

    /**
     * Removes all connections between actors and resets the actors to being susceptible.
     */
    public void resetActors() {
        clearConnections();
        Iterator<Actor> actorsIt = this.actors.iterator();
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
        if (this.actors == null || this.actors.isEmpty()) {
            return;
        }

        // actors performing action in random order
        Collections.shuffle(this.actors);

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
    public void toggleInfection(long actorId, DiseaseSpecs diseaseSpecs) {
        Iterator<Actor> actorsIt = this.actors.iterator();
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
    private final void notifyActorAdded(long actorId) {
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
    private final void notifyActorRemoved(long actorId) {
        Iterator<ActorAmountListener> listenersIt = this.actorAmountListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActorRemoved(actorId);
        }
    }

}

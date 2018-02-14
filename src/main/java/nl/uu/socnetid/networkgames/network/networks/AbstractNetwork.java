package nl.uu.socnetid.networkgames.network.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.stats.GlobalActorStats;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // logger
    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // set of actors
    private List<Actor> actors;

    // stats
    private GlobalActorStats actorStats = new GlobalActorStats();
    private double cumulatedRisk = 0.0;


    /**
     * Constructor.
     *
     * @param actors
     *          list of actors in the network
     */
    protected AbstractNetwork(List<Actor> actors) {
        this.actors = actors;

        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            updateActorStats(actor, true);
        }
    }

    /**
     * Updates the actor stats.
     *
     * @param actor
     *          the actor with the information to be updated
     * @param add
     *          true if a actor is being added, false if a actor is being removed
     */
    private void updateActorStats(Actor actor, boolean add) {

        if (add) {
            this.actorStats.incActorsOverall();
            this.cumulatedRisk += actor.getRiskFactor();
        } else {
            this.actorStats.decActorsOverall();
            this.cumulatedRisk -= actor.getRiskFactor();
        }

        // risk
        this.actorStats.setAvRisk(this.actors.size() > 0 ?
                this.cumulatedRisk / this.actors.size() :
                    0.0);

        if (actor.getRiskFactor() == 1.0) {
            if (add) {
                this.actorStats.incRiskNeutrals();
            } else {
                this.actorStats.decRiskNeutrals();
            }

        } else if (actor.getRiskFactor() > 1.0) {
            if (add) {
                this.actorStats.incRiskAverse();
            } else {
                this.actorStats.decRiskAverse();
            }

        } else if (actor.getRiskFactor() < 1.0) {
            if (add) {
                this.actorStats.incRiskSeeking();
            } else {
                this.actorStats.decRiskSeeking();
            }

        } else {
            logger.warn("Undefined risk behavior for actor " + actor.getId()
            + ": " + actor.getRiskFactor());
        }

        // disease
        switch (actor.getDiseaseGroup()) {
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
                logger.warn("Unknown disease group for actor " + actor.getId()
                        + ": " + actor.getDiseaseGroup());
        }
    }

    /**
     * Resets the actor stats to all actors being susceptible.
     */
    private void resetActorStats() {
        this.actorStats.setSusceptibles(this.actors.size());
        this.actorStats.setInfected(0);
        this.actorStats.setRecovered(0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#addActor(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void addActor(Actor actor) {
        this.actors.add(actor);

        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }

        // update stats
        updateActorStats(actor, true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#removeActor()
     */
    @Override
    public void removeActor() {
        if (this.actors.size() == 0) {
            return;
        }
        Actor actor = this.actors.get(this.actors.size() - 1);

        // remove
        actor.destroy();
        this.actors.remove(actor);


        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }

        // update stats
        updateActorStats(actor, false);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#getActors()
     */
    @Override
    public List<Actor> getActors() {
        return this.actors;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#getActor(long)
     */
    @Override
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

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#getConnectionsForActor(nl.uu.socnetid.networkgames.
     * actors.Actor)
     */
    @Override
    public List<Actor> getConnectionsOfActor(Actor actor) {
        return actor.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().removeAllConnections();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#resetActors()
     */
    @Override
    public void resetActors() {
        clearConnections();
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.makeSusceptible();

            // reset actor stats
            resetActorStats();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#infectRandomActor(
     * nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
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

            // update stats
            if (actor.isSusceptible()) {
                this.actorStats.decSusceptibles();
            } else if (actor.isRecovered()) {
                this.actorStats.decRecovered();
            } else {
                logger.warn("Unable to (force-) infect an actor that is either susceptible, nor recovered");
                return;
            }
            actor.forceInfect(diseaseSpecs);
            this.actorStats.incInfected();

            return;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#toggleInfection(long,
     * nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void toggleInfection(long actorId, DiseaseSpecs diseaseSpecs) {
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.getId() == actorId) {

                switch (actor.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        actor.forceInfect(diseaseSpecs);
                        this.actorStats.decSusceptibles();
                        this.actorStats.incInfected();
                        break;

                    case INFECTED:
                        actor.cure();
                        this.actorStats.decInfected();
                        this.actorStats.incRecovered();
                        break;

                    case RECOVERED:
                        actor.makeSusceptible();
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
     * @see nl.uu.socnetid.networkgames.network.networks.Network#getGlobalActorStats()
     */
    @Override
    public GlobalActorStats getGlobalActorStats() {

        // TODO move to StatsComputer.java
        return this.actorStats;
    }

}

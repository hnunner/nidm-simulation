package nl.uu.socnetid.netgame.stats;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.simulation.Simulation;

/**
 * @author Hendrik Nunner
 */
public final class StatsComputer {

    private static final Logger logger = Logger.getLogger(StatsComputer.class);

    /** Private construtor. Inhibits unwanted instantiation of class. */
    private StatsComputer() { }


    /**
     * Computes the first order degree of an actor.
     *
     * @param actor
     *          the actor to compute the first degree for
     * @return the first order degree of the actor
     */
    public static int computeFirstOrderDegree(Actor actor) {
        return actor.getDegree();
    }

    /**
     * Computes the second order degree of an actor.
     *
     * @param actor
     *          the actor to compute the second degree for
     * @return the second order degree of the actor
     */
    public static int computeSecondOrderDegree(Actor actor) {
        return StatsComputer.computeLocalActorConnectionsStats(actor).getM();
    }

    /**
     * Computes the closeness of an actor. Based on Buechel & Buskens (2013) formula (1), p.163.
     *
     * @param actor
     *          the actor to compute the closeness for
     * @return the closeness of the actor
     */
    public static double computeCloseness(Actor actor) {

        // Note: M = n, see Buechel & Buskens (2013), p. 162
        double n = actor.getCoActors().size() + 1;              // all co-actors plus the actor himself
        double M = n;
        double cumulatedDistance = 0.0;

        // distances
        // 1st calculate shortest paths for all co-actors
        DijkstraShortestPath dsp = new DijkstraShortestPath();
        dsp.executeShortestPaths(actor);
        Iterator<Actor> coActorsIt = actor.getCoActors().iterator();
        while (coActorsIt.hasNext()) {
            Actor coActor = coActorsIt.next();
            Integer shortestPathLength = dsp.getShortestPathLength(coActor);
            // if path exists
            if (shortestPathLength != null) {
                // distance of connected nodes
                cumulatedDistance += Double.valueOf(shortestPathLength);
            } else {
                // distance of not connected nodes = n
                cumulatedDistance += n;
            }
        }

        // average distance including normalization
        return (M / (M - 1)) - (cumulatedDistance / ((M - 1) * (n - 1)));
    }

    /**
     * Computes the global network stats of a given network.
     *
     * @param network
     *          the network to compute the global stats for
     * @return the global network stats
     */
    public static GlobalNetworkStats computeGlobalNetworkStats(Network network) {

        Collection<Actor> actors = network.getActors();

        boolean stable = true;
        int connections = 0;
        double avDegree = 0.0;

        // implement diameter and average distance (if it adds to understanding)
        int diameter = 0;
        double avDistance = 0.0;

        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            stable &= actor.isSatisfied();
            connections += actor.getConnections().size();
        }
        avDegree = actors.size() == 0 ? 0.0 : (double) connections / actors.size();
        connections /= 2;

        return new GlobalNetworkStats(stable, connections, avDegree, diameter, avDistance);
    }

    /**
     * Computes the global actor stats for the given network.
     *
     * @param network
     *          the network to compute the global network stats for
     * @return the global actor stats for the given network
     */
    public static GlobalActorStats computeGlobalActorStats(Network network) {

        // all actors
        Collection<Actor> actors = network.getActors();
        int n = actors.size();

        // disease groups
        int nS = 0;
        int nI = 0;
        int nR = 0;

        // risk behavior
        int nRiskAverse = 0;
        int nRiskNeutral = 0;
        int nRiskSeeking = 0;
        double cumRisk = 0.0;

        // check all actors
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();

            // disease group
            switch (actor.getDiseaseGroup()) {
                case SUSCEPTIBLE:
                    nS++;
                    break;

                case INFECTED:
                    nI++;
                    break;

                case RECOVERED:
                    nR++;
                    break;

                default:
                    logger.warn("Unknown disease group: " + actor.getDiseaseGroup());
            }

            // risk behavior
            double riskFactor = actor.getRiskFactor();
            if (riskFactor > 1) {
                nRiskAverse++;
            } else if (riskFactor < 1) {
                nRiskSeeking++;
            } else {
                nRiskNeutral++;
            }
            cumRisk += riskFactor;
        }

        return new GlobalActorStats(n, nS, nI, nR, nRiskAverse, nRiskNeutral, nRiskSeeking, cumRisk / n);
    }

    public static GlobalSimulationStats computeGlobalSimulationStats(Simulation simulation) {
        if (simulation == null) {
            return new GlobalSimulationStats(false, 0);
        }
        return new GlobalSimulationStats(simulation.isRunning(), simulation.getRounds());
    }

    /**
     * Computes the stats for a single actor's connections.
     *
     * @param actor
     *          the actor
     * @return the stats for a single actor's connections.
     */
    public static LocalActorConnectionsStats computeLocalActorConnectionsStats(Actor actor) {
        return StatsComputer.computeLocalActorConnectionsStats(actor, actor.getConnections());
    }

    /**
     * Computes the stats for a single actor's connections.
     *
     * @param actor
     *          the actor
     * @param connections
     *          the connections - given explicitly and not used from the actor's existing connections,
     *          because this might be used to compare the actor's current connections with potentially
     *          altered connections
     * @return the stats for a single actor's connections.
     */
    public static LocalActorConnectionsStats computeLocalActorConnectionsStats(Actor actor,
            Collection<Actor> connections) {

        // number of connections
        int nS = 0;
        int nI = 0;
        int nR = 0;
        int  m = 0;

        // indirect connections considered only once
        List<Actor> consideredIndirectBenefits = new LinkedList<Actor>();

        // for every direct connection
        Iterator<Actor> directIt = connections.iterator();
        while (directIt.hasNext()) {

            // no direct connection? do nothing
            Actor directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            // disease group of direct connection
            switch (directConnection.getDiseaseGroup()) {
                case SUSCEPTIBLE:
                    nS++;
                    break;

                case INFECTED:
                    nI++;
                    break;

                case RECOVERED:
                    nR++;
                    break;

                default:
                    logger.warn("Unhandled disease group: " + directConnection.getDiseaseGroup());
            }

            // no indirect connections --> go to next direct connection
            Collection<Actor> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            // for every indirect connection at distance 2
            Iterator<Actor> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Actor indirectConnection = indirectIt.next();
                // no benefit from self
                if (indirectConnection.equals(actor)
                        // no double benefits for indirect connections being also direct connections
                        || connections.contains(indirectConnection)
                        // no double benefits for indirect connections that have been booked already
                        || consideredIndirectBenefits.contains(indirectConnection)) {
                    continue;
                }
                m++;
                consideredIndirectBenefits.add(indirectConnection);
            }
        }
        return new LocalActorConnectionsStats(nS, nI, nR, m);
    }

    /**
     * Computes the actor's probability of getting infected.
     *
     * @param actor
     *          the actor to compute the probability for
     * @param nI
     *          the number of infected direct connections
     * @return the actor's probability of getting infected
     */
    public static double computeProbabilityOfInfection(Actor actor, int nI) {
        return 1 - Math.pow((1 - actor.getDiseaseSpecs().getGamma()), nI);
    }

}
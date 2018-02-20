package nl.uu.socnetid.networkgames.stats;

import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 * @author Hendrik Nunner
 */
public final class StatsComputer {

    /** Private construtor. Inhibits unwanted instantiation of class. */
    private StatsComputer() { }


    /**
     * Computes the first degree of an actor.
     *
     * @param actor
     *          the actor to compute the first degree for
     * @return the first degree of the actor
     */
    public static int computeFirstDegree(Actor actor) {
        return actor.getConnections().size();
    }

    /**
     * Computes the second degree of an actor.
     *
     * @param actor
     *          the actor to compute the second degree for
     * @return the second degree of the actor
     */
    public static int computeSecondDegree(Actor actor) {
        // TODO implement
        return 0;
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
        double first = M / (M - 1);
        double under = (M - 1) * (n - 1);
        double second = cumulatedDistance / under;
        double closeness = first - second;
        return closeness;
    }

    /**
     * Computes the global network stats of a given network.
     *
     * @param network
     *          the network to compute the global stats for
     * @return the global network stats
     */
    public static GlobalNetworkStats computeGlobalNetworkStats(Network network) {

        List<Actor> actors = network.getActors();

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



}

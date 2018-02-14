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
     * Computes the degree of an actor.
     *
     * @param actor
     *          the actor to compute the degree for
     * @return the degree of the actor
     */
    public static int computeDegree(Actor actor) {
        return actor.getConnections().size();
    }

    /**
     * Computes the closeness of an actor.
     *
     * @param actor
     *          the actor to compute the closeness for
     * @return the closeness of the actor
     */
    public static double computeCloseness(Actor actor) {
        double cs = 0.0;

        // TODO implement

        return cs;
    }

    /**
     * Computes the betweenness of an actor.
     *
     * @param actor
     *          the actor to compute the betweenness for
     * @return the betweenness of the actor
     */
    public static double computeBetweenness(Actor actor) {
        double bs = 0.0;

        // TODO implement

        return bs;
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

        // TODO implement diameter and average distance (if it adds to understanding)
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
     * Computes the network distance between two actors.
     *
     * @param actor1
     *          the first actor
     * @param actor2
     *          the second actor
     * @return the distance between actor 1 and actor 2
     */
    public static int computeDistance(Actor actor1, Actor actor2) {

        // TODO implement

/*        BFS(start_node, goal_node) {
            for(all nodes i) visited[i] = false; // anfangs sind keine Knoten besucht
            queue.push(start_node);              // mit Start-Knoten beginnen
            visited[start_node] = true;
            while(! queue.empty() ) {            // solange queue nicht leer ist
             node = queue.pop();                 // erstes Element von der queue nehmen
             if(node == goal_node) {
              return true;                       // testen, ob Ziel-Knoten gefunden
             }
             foreach(child in expand(node)) {    // alle Nachfolge-Knoten, …
              if(visited[child] == false) {      // … die noch nicht besucht wurden …
               queue.push(child);                // … zur queue hinzufügen…
               visited[child] = true;            // … und als bereits gesehen markieren
              }
             }
            }
            return false;                        // Knoten kann nicht erreicht werden
           }
*/
        return 0;
    }

}

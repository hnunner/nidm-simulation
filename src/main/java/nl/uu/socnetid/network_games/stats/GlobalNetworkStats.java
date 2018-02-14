package nl.uu.socnetid.network_games.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalNetworkStats {

    private boolean stable = false;
    private int connections = 0;
    private double avDegree = 0.0;
    private int diameter = 0;
    private double avDistance = 0.0;

    /**
     * Constructor
     *
     * @param stable
     *          flag whether the network is stable
     * @param connections
     *          the amount of connections within the network
     * @param avDegree
     *          the average degree within the network
     * @param diameter
     *          the diameter of the network
     * @param avDistance
     *          the average distance of the network
     */
    public GlobalNetworkStats(boolean stable, int connections, double avDegree, int diameter, double avDistance) {
        this.stable = stable;
        this.connections = connections;
        this.avDegree = avDegree;
        this.diameter = diameter;
        this.avDistance = avDistance;
    }


    /**
     * @return the stable
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * @return the connections
     */
    public int getConnections() {
        return connections;
    }

    /**
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @return the diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * @return the avDistance
     */
    public double getAvDistance() {
        return avDistance;
    }

}

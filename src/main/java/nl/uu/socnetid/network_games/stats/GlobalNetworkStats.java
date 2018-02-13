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
     * @return the stable
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * @param stable the stable to set
     */
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    /**
     * @return the connections
     */
    public int getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(int connections) {
        this.connections = connections;
    }

    /**
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @param avDegree the avDegree to set
     */
    public void setAvDegree(double avDegree) {
        this.avDegree = avDegree;
    }

    /**
     * @return the diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    /**
     * @return the avDistance
     */
    public double getAvDistance() {
        return avDistance;
    }

    /**
     * @param avDistance the avDistance to set
     */
    public void setAvDistance(double avDistance) {
        this.avDistance = avDistance;
    }

}

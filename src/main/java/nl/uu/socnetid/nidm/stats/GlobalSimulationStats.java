package nl.uu.socnetid.nidm.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalSimulationStats {

    private final boolean running;
    private final int round;

    /**
     * Constructor.
     *
     * @param running
     *          flag indicating whether the simulation is running or not
     * @param round
     *          the amount of rounds the simulation has computed yet
     */
    public GlobalSimulationStats(boolean running, int round) {
        this.running = running;
        this.round = round;
    }


    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @return the round
     */
    public int getRound() {
        return round;
    }

}

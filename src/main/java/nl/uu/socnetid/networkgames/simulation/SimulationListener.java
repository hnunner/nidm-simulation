package nl.uu.socnetid.networkgames.simulation;

/**
 * @author Hendrik Nunner
 */
public interface SimulationListener {

    /**
     * Entry point for rounds being finished notifications.
     *
     * @param simulation
     *          the simulation that finished the round
     */
    void notifyRoundFinished(final Simulation simulation);

}

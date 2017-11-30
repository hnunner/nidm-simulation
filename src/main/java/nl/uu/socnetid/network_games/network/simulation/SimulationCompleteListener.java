package nl.uu.socnetid.network_games.network.simulation;

/**
 * @author Hendrik Nunner
 */
public interface SimulationCompleteListener {

    /**
     * Entry point for notification of completed simulation routines.
     *
     * @param simulation
     *          the completed simulation
     */
    void notifyOfSimulationComplete(final Simulation simulation);

}

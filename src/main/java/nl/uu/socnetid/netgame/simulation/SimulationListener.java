package nl.uu.socnetid.netgame.simulation;

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

    /**
     * Entry point for infection being defeated notifications.
     *
     * @param simulation
     *          the simulation in which the infection has been defeated
     */
    void notifyInfectionDefeated(final Simulation simulation);

    /**
     * Entry point for simulation being finished notifications.
     *
     * @param simulation
     *          the simulation that has finished
     */
    void notifySimulationFinished(final Simulation simulation);

}

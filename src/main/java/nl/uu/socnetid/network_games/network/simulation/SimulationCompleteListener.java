package nl.uu.socnetid.network_games.network.simulation;

/**
 * @author Hendrik Nunner
 */
public interface SimulationCompleteListener {

    /**
     * Entry point for completed simulation notification.
     *
     * @param simulation
     *          the finished simulation
     */
    void notify(final Simulation simulation);

}

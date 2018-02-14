package nl.uu.socnetid.networkgames.network.simulation;

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

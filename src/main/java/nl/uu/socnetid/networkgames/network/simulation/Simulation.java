package nl.uu.socnetid.networkgames.network.simulation;

/**
 * @author Hendrik Nunner
 */
public interface Simulation {

    /**
     * Adds a listener to be notified when a simulation event occurs.
     *
     * @param listener
     *          the listener to be notified
     */
    void addListener(final SimulationCompleteListener listener);

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    void removeListener(final SimulationCompleteListener listener);

}

package nl.uu.socnetid.network_games.network.simulation;

/**
 * @author Hendrik Nunner
 */
public interface Simulation {

    /**
     * Adds a listener to be notified once a simulation is finished.
     *
     * @param listener
     *          the listener to be notified
     */
    void addListener(final SimulationCompleteListener listener);

    /**
     * Removes a listener used to be notified once a simulation is finished.
     *
     * @param listener
     *          the listener to be removed
     */
    void removeListener(final SimulationCompleteListener listener);

    /**
     * Gets a status message of the simulation.
     *
     * @return a status message
     */
    String getStatusMessage();

}

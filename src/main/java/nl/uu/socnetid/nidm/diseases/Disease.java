package nl.uu.socnetid.nidm.diseases;

/**
 * @author Hendrik Nunner
 */
public interface Disease {

    /**
     * Evolves the disease: increases the duration counter and
     * checks whether the disease is being defeated already.
     */
    void evolve();

    /**
     * Checks whether the disease is infectious.
     *
     * @return true if the disease is infectious, false otherwise
     */
    boolean isInfectious();

    /**
     * Checks whether the disease is cured.
     *
     * @return true if the disease is cured, false otherwise
     */
    boolean isCured();

    /**
     * Gets the time remaining until the disease is cured.
     *
     * @return the time in round until the disease is cured
     */
    int getTimeUntilCured();

    /**
     * Gets the {@link DiseaseSpecs}.
     *
     * @return the {@link DiseaseSpecs}
     */
    DiseaseSpecs getDiseaseSpecs();

}

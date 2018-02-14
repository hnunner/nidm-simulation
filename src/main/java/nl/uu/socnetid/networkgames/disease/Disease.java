package nl.uu.socnetid.networkgames.disease;

/**
 * @author Hendrik Nunner
 */
public interface Disease {

    void evolve();

    boolean isTransmitted();
    boolean isInfectious();
    boolean isDefeated();

    int getTimeUntilRecovered();

    DiseaseSpecs getDiseaseSpecs();

}

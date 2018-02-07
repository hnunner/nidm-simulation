package nl.uu.socnetid.network_games.disease;

/**
 * @author Hendrik Nunner
 */
public interface Disease {

    Disease copy();
    void evolve();

    boolean isTransmitted();
    boolean isInfectious();
    boolean isDefeated();

    double getDelta();
    double getMu();
}

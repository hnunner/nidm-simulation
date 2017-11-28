package nl.uu.socnetid.network_games.disease;

/**
 * @author Hendrik Nunner
 */
public interface Disease {

    void evolve();

    boolean isTransmitted();
    boolean isInfectious();
    boolean isVisible();
    boolean isDefeated();
}

package nl.uu.socnetid.network_games.disease;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Hendrik Nunner
 */
public class TwoStageDisease implements Disease {

    // duration a disease lasts
    private static final int OVERALL_DURATION = 10;

    // transmission rate
    private static final double TRANSMISSION_RATE = 0.1;

    // time the disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;


    /**
     * Constructor initializations.
     */
    public TwoStageDisease() {
        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS_VISIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#relieve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration > OVERALL_DURATION) {
            this.diseaseState = DiseaseState.DEFEATED;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        return this.diseaseState == DiseaseState.INFECTIOUS_VISIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isVisible()
     */
    @Override
    public boolean isVisible() {
        return this.diseaseState == DiseaseState.INFECTIOUS_VISIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isDefeated()
     */
    @Override
    public boolean isDefeated() {
        return this.diseaseState == DiseaseState.DEFEATED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isTransmitted()
     */
    @Override
    public boolean isTransmitted() {
        return ThreadLocalRandom.current().nextDouble() <= TRANSMISSION_RATE;
    }

}

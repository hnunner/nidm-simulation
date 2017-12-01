package nl.uu.socnetid.network_games.disease;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Hendrik Nunner
 */
public class TwoStageDisease implements Disease {

    // duration a disease lasts
    private final int duration;
    // transmission rate
    private final double transmissionRate;
    // costs to treat the disease
    private final double treatmentCosts;

    // time the disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;


    /**
     * Constructor initializations.
     *
     * @param duration
     *          the duration the disease takes to recover
     * @param transmissionRate
     *          the rate at which the disease is being transmitted
     * @param treatmentCosts
     *          the costs to treat the disease
     */
    public TwoStageDisease(int duration, double transmissionRate, double treatmentCosts) {
        this.duration = duration;
        this.transmissionRate = transmissionRate;
        this.treatmentCosts = treatmentCosts;

        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS_VISIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#relieve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration > this.duration) {
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
        return ThreadLocalRandom.current().nextDouble() <= this.transmissionRate;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#getCureCosts()
     */
    @Override
    public double getTreatmentCosts() {
        return this.treatmentCosts;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#copy()
     */
    @Override
    public Disease copy() {
        return new TwoStageDisease(this.duration, this.transmissionRate, this.treatmentCosts);
    }

}

package nl.uu.socnetid.network_games.disease;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Hendrik Nunner
 */
public class ThreeStageDisease implements Disease {

    // duration a disease lasts
    private final int duration;
    private final int invisible;
    private final int visible;

    // treatment costs
    private final double treatmentCosts;
    // transmission rate
    private final double transmissionRate;

    // time the disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;


    /**
     * Constructor initializations.
     *
     * @param duration
     *          the amount of rounds the disease requires for recovery
     * @param invisible
     *          the amount of rounds a disease is invisible in the beginning and the end
     * @param treatmentCosts
     *          the utility required each round to care for infected direct connections
     * @param transmissionRate
     *          the probability to transmit the disease each round
     */
    public ThreeStageDisease(int duration, int invisible, double treatmentCosts, double transmissionRate) {
        this.duration = duration;
        this.invisible = invisible;
        this.visible = duration - 2*invisible;
        this.treatmentCosts = treatmentCosts;
        this.transmissionRate = transmissionRate;

        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS_NOT_VISIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#relieve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration <= this.invisible) {
            this.diseaseState = DiseaseState.INFECTIOUS_NOT_VISIBLE;

        } else if (currDuration <= this.invisible + this.visible) {
            this.diseaseState = DiseaseState.INFECTIOUS_VISIBLE;

        } else if (currDuration <= this.invisible + this.visible + this.visible) {
            this.diseaseState = DiseaseState.INFECTIOUS_NOT_VISIBLE;

        } else {
            this.diseaseState = DiseaseState.DEFEATED;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        return (this.diseaseState == DiseaseState.INFECTIOUS_NOT_VISIBLE
                || this.diseaseState == DiseaseState.INFECTIOUS_VISIBLE);
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
     * @see nl.uu.socnetid.network_games.disease.Disease#getTreatmentCosts()
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
        return new ThreeStageDisease(this.duration, this.invisible, this.treatmentCosts, this.transmissionRate);
    }

}

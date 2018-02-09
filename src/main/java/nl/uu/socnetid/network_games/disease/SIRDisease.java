package nl.uu.socnetid.network_games.disease;

import java.util.concurrent.ThreadLocalRandom;

import nl.uu.socnetid.network_games.disease.types.DiseaseState;

/**
 * @author Hendrik Nunner
 */
public class SIRDisease implements Disease {

    // the characteristics of the disease
    private final DiseaseSpecs diseaseSpecs;

    // time the already disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;


    /**
     * Constructor initializations.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease
     */
    protected SIRDisease(DiseaseSpecs diseaseSpecs) {
        this.diseaseSpecs = diseaseSpecs;
        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#evolve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration >= this.diseaseSpecs.getTau()) {
            this.diseaseState = DiseaseState.DEFEATED;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#getDiseaseSpecs()
     */
    @Override
    public DiseaseSpecs getDiseaseSpecs() {
        return this.diseaseSpecs;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        return (this.diseaseState == DiseaseState.INFECTIOUS);
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
        return ThreadLocalRandom.current().nextDouble() <= this.diseaseSpecs.getGamma();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#getTimeRemaining()
     */
    @Override
    public int getTimeUntilRecovered() {
        return this.getDiseaseSpecs().getTau() - this.currDuration;
    }

}

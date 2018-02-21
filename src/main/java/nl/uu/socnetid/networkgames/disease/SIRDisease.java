package nl.uu.socnetid.networkgames.disease;

import nl.uu.socnetid.networkgames.disease.types.DiseaseState;

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
     * @see nl.uu.socnetid.networkgames.disease.Disease#evolve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration >= this.diseaseSpecs.getTau()) {
            this.diseaseState = DiseaseState.DEFEATED;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.disease.Disease#getDiseaseSpecs()
     */
    @Override
    public DiseaseSpecs getDiseaseSpecs() {
        return this.diseaseSpecs;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.disease.Disease#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        return (this.diseaseState == DiseaseState.INFECTIOUS);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.disease.Disease#isDefeated()
     */
    @Override
    public boolean isCured() {
        return this.diseaseState == DiseaseState.DEFEATED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.disease.Disease#getTimeRemaining()
     */
    @Override
    public int getTimeUntilCured() {
        return this.getDiseaseSpecs().getTau() - this.currDuration;
    }

}

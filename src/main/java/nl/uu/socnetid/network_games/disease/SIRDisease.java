package nl.uu.socnetid.network_games.disease;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Hendrik Nunner
 */
public class SIRDisease implements Disease {

    private final int tau;
    private final double delta;
    private final double gamma;
    private final double mu;

    // time the already disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;


    /**
     * Constructor initializations.
     *
     * @param tau
     *          the duration a disease requires to recover from in rounds
     * @param delta
     *          the severity of the disease represented by the amount of punishment for having a disease
     * @param gamma
     *          the transmission rate - the probability a disease is spread between an infected and a non-infected
     *          agent per round
     * @param mu
     *          the factor that increases maintenance costs for infected connections
     */
    public SIRDisease(int tau, double delta, double gamma, double mu) {
        this.tau = tau;
        this.delta = delta;
        this.gamma = gamma;
        this.mu = mu;

        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#evolve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration >= this.tau) {
            this.diseaseState = DiseaseState.DEFEATED;
        }
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
        return ThreadLocalRandom.current().nextDouble() <= this.gamma;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#getDelta()
     */
    @Override
    public double getDelta() {
        return this.delta;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#getMu()
     */
    @Override
    public double getMu() {
        return this.mu;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.disease.Disease#copy()
     */
    @Override
    public Disease copy() {
        return new SIRDisease(this.tau, this.delta, this.gamma, this.mu);
    }

}

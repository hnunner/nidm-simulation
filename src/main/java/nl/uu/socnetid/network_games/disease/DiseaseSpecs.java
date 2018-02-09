package nl.uu.socnetid.network_games.disease;

import java.util.Arrays;

import nl.uu.socnetid.network_games.disease.types.DiseaseType;

/**
 * @author Hendrik Nunner
 */
public class DiseaseSpecs {

    private final DiseaseType diseaseType;
    private final int tau;
    private final double delta;
    private final double gamma;
    private final double mu;

    /**
     * Constructor initializations.
     *
     * @param diseaseType
     *          the type of disease
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
    public DiseaseSpecs(DiseaseType diseaseType, int tau, double delta, double gamma, double mu) {
        this.diseaseType = diseaseType;
        this.tau = tau;
        this.delta = delta;
        this.gamma = gamma;
        this.mu = mu;
    }

    /**
     * @return the type of disease
     */
    public DiseaseType getDiseaseType() {
        return this.diseaseType;
    }

    /**
     * @return the severity of the disease represented by the amount of punishment for having a disease
     */
    public double getDelta() {
        return this.delta;
    }

    /**
     * @return the duration a disease requires to recover from in rounds
     */
    public int getTau() {
        return this.tau;
    }

    /**
     * @return the transmission rate - the probability a disease is spread between an infected and a non-infected
     *          agent per round
     */
    public double getGamma() {
        return this.gamma;
    }

    /**
     * @return the factor that increases maintenance costs for infected connections
     */
    public double getMu() {
        return this.mu;
    }

    /**
     * @return the name of the disease as presented in the stats window
     */
    public String getStatsName() {
        return this.diseaseType.toString();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DiseaseSpecs)) {
            return false;
        }
        DiseaseSpecs specs2 = (DiseaseSpecs) obj;
        if (specs2.getDiseaseType() != this.diseaseType) {
            return false;
        }
        if (specs2.getTau() != this.tau) {
            return false;
        }
        if (specs2.getDelta() != this.delta) {
            return false;
        }
        if (specs2.getGamma() != this.gamma) {
            return false;
        }
        if (specs2.getMu() != this.mu) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.diseaseType,
                this.tau,
                this.delta,
                this.gamma,
                this.mu
         });
    }

}

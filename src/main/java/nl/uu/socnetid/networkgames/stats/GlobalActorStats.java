package nl.uu.socnetid.networkgames.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalActorStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int nRiskAverse;
    private final int nRiskNeutral;
    private final int nRiskSeeking;
    private final double avRisk;

    /**
     * Constructor.
     *
     * @param n
     *          the overall amount of actors
     * @param nS
     *          the amount of susceptibles
     * @param nI
     *          the amount of infected
     * @param nR
     *          the amount of recovered
     * @param nRiskAverse
     *          the amount of risk averse
     * @param nRiskNeutral
     *          the amount of risk neutral
     * @param nRiskSeeking
     *          the amount of risk seeking
     * @param avRisk
     *          the average risk factor
     */
    public GlobalActorStats(int n, int nS, int nI, int nR, int nRiskAverse, int nRiskNeutral, int nRiskSeeking,
            double avRisk) {
        this.n = n;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.nRiskAverse = nRiskAverse;
        this.nRiskNeutral = nRiskNeutral;
        this.nRiskSeeking = nRiskSeeking;
        this.avRisk = avRisk;
    }

    /**
     * @return the n
     */
    public int getN() {
        return n;
    }

    /**
     * @return the nS
     */
    public int getnS() {
        return nS;
    }

    /**
     * @return the nI
     */
    public int getnI() {
        return nI;
    }

    /**
     * @return the nR
     */
    public int getnR() {
        return nR;
    }

    /**
     * @return the nRiskAverse
     */
    public int getnRiskAverse() {
        return nRiskAverse;
    }

    /**
     * @return the nRiskNeutral
     */
    public int getnRiskNeutral() {
        return nRiskNeutral;
    }

    /**
     * @return the nRiskSeeking
     */
    public int getnRiskSeeking() {
        return nRiskSeeking;
    }

    /**
     * @return the avRisk
     */
    public double getAvRisk() {
        return avRisk;
    }

}

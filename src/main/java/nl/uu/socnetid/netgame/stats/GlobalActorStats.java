package nl.uu.socnetid.netgame.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalActorStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int nRSigmaAverse;
    private final int nRSigmaNeutral;
    private final int nRSigmaSeeking;
    private final double avRSigma;
    private final int nRPiAverse;
    private final int nRPiNeutral;
    private final int nRPiSeeking;
    private final double avRPi;

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
     * @param nRSigmaAverse
     *          the amount of risk averse (disease severity)
     * @param nRSigmaNeutral
     *          the amount of risk neutral (disease severity)
     * @param nRSigmaSeeking
     *          the amount of risk seeking (disease severity)
     * @param avRSigma
     *          the average risk factor (disease severity)
     * @param nRPiAverse
     *          the amount of risk averse (probability of infection)
     * @param nRPiNeutral
     *          the amount of risk neutral (probability of infection)
     * @param nRPiSeeking
     *          the amount of risk seeking (probability of infection)
     * @param avRPi
     *          the average risk factor (probability of infection)
     */
    public GlobalActorStats(int n, int nS, int nI, int nR,
            int nRSigmaAverse, int nRSigmaNeutral, int nRSigmaSeeking, double avRSigma,
            int nRPiAverse, int nRPiNeutral, int nRPiSeeking, double avRPi) {
        this.n = n;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.nRSigmaAverse = nRSigmaAverse;
        this.nRSigmaNeutral = nRSigmaNeutral;
        this.nRSigmaSeeking = nRSigmaSeeking;
        this.avRSigma= avRSigma;
        this.nRPiAverse = nRPiAverse;
        this.nRPiNeutral = nRPiNeutral;
        this.nRPiSeeking = nRPiSeeking;
        this.avRPi = avRPi;
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
     * @return the nRSigmaAverse
     */
    public int getnRSigmaAverse() {
        return nRSigmaAverse;
    }

    /**
     * @return the nRSigmaNeutral
     */
    public int getnRSigmaNeutral() {
        return nRSigmaNeutral;
    }

    /**
     * @return the nRSigmaSeeking
     */
    public int getnRSigmaSeeking() {
        return nRSigmaSeeking;
    }

    /**
     * @return the avRSigma
     */
    public double getAvRSigma() {
        return avRSigma;
    }

    /**
     * @return the nRPiAverse
     */
    public int getnRPiAverse() {
        return nRPiAverse;
    }

    /**
     * @return the nRPiNeutral
     */
    public int getnRPiNeutral() {
        return nRPiNeutral;
    }

    /**
     * @return the nRPiSeeking
     */
    public int getnRPiSeeking() {
        return nRPiSeeking;
    }

    /**
     * @return the avRPi
     */
    public double getAvRPi() {
        return avRPi;
    }

}

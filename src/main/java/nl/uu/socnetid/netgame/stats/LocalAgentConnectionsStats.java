package nl.uu.socnetid.netgame.stats;

/**
 * @author Hendrik Nunner
 */
public class LocalAgentConnectionsStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int m;
    private final int mS;
    private final int mI;
    private final int mR;

    /**
     * Constructor.
     *
     * @param nS
     *          the amount of susceptible direct connections
     * @param nI
     *          the amount of infected direct connections
     * @param nR
     *          the amount of recovered direct connections
     * @param mS
     *          the amount of susceptible indirect connections
     * @param mI
     *          the amount of infected indirect connections
     * @param mR
     *          the amount of recovered indirect connections
     */
    public LocalAgentConnectionsStats(int nS, int nI, int nR, int mS, int mI, int mR) {
        this.n = nS + nI + nR;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.m = mS + mI + mR;
        this.mS = mS;
        this.mI = mI;
        this.mR = mR;
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
     * @return the m
     */
    public int getM() {
        return m;
    }

    /**
     * @return the mS
     */
    public int getmS() {
        return mS;
    }

    /**
     * @return the mI
     */
    public int getmI() {
        return mI;
    }

    /**
     * @return the mR
     */
    public int getmR() {
        return mR;
    }

}

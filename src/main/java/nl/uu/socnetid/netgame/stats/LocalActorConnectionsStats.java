package nl.uu.socnetid.netgame.stats;

/**
 * @author Hendrik Nunner
 */
public class LocalActorConnectionsStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int m;

    /**
     * Constructor.
     *
     * @param nS
     *          the amount of susceptible direct connections
     * @param nI
     *          the amount of infected direct connections
     * @param nR
     *          the amount of recovered direct connections
     * @param m
     *          the amount of indirect direct connections
     */
    public LocalActorConnectionsStats(int nS, int nI, int nR, int m) {
        this.n = nS + nI + nR;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.m = m;
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

}

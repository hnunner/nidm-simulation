package nl.uu.socnetid.networkgames.stats;

/**
 * @author Hendrik Nunner
 */
public class LocalActorConnectionsStats {

    private int n;
    private int nS;
    private int nI;
    private int nR;
    private int m;

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
     * @param n the n to set
     */
    public void setN(int n) {
        this.n = n;
    }

    /**
     * @return the nS
     */
    public int getnS() {
        return nS;
    }

    /**
     * @param nS the nS to set
     */
    public void setnS(int nS) {
        this.nS = nS;
    }

    /**
     * @return the nI
     */
    public int getnI() {
        return nI;
    }

    /**
     * @param nI the nI to set
     */
    public void setnI(int nI) {
        this.nI = nI;
    }

    /**
     * @return the nR
     */
    public int getnR() {
        return nR;
    }

    /**
     * @param nR the nR to set
     */
    public void setnR(int nR) {
        this.nR = nR;
    }

    /**
     * @return the m
     */
    public int getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(int m) {
        this.m = m;
    }

}

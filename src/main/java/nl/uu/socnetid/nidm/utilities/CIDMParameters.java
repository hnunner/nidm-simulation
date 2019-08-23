package nl.uu.socnetid.nidm.utilities;

/**
 * @author Hendrik Nunner
 */
public class CIDMParameters {

    // social benefits
    private double[] alphas;
    private double[] kappas;
    private double[] betas;
    private double[] lamdas;
    // social maintenance costs
    private double[] cs;
    private double[] mus;
    // potential harm of infections (currently: r_pi == r_sigma)
    private double[] sigmas;
    private double[] gammas;
    private boolean rsEqual;
    private double[] rSigmas;
    private double[] rPis;
    // network
    private int[] Ns;
    private boolean[] iotas;
    private double[] phis;
    // simulation
    private int zeta;
    private int epsilon;
    private int[] taus;
    private int simsPerParameterCombination;


    /**
     * @return the alphas
     */
    public double[] getAlphas() {
        return alphas;
    }

    /**
     * @param alphas the alphas to set
     */
    public void setAlphas(double[] alphas) {
        this.alphas = alphas;
    }

    /**
     * @return the kappas
     */
    public double[] getKappas() {
        return kappas;
    }

    /**
     * @param kappas the kappas to set
     */
    public void setKappas(double[] kappas) {
        this.kappas = kappas;
    }

    /**
     * @return the betas
     */
    public double[] getBetas() {
        return betas;
    }

    /**
     * @param betas the betas to set
     */
    public void setBetas(double[] betas) {
        this.betas = betas;
    }

    /**
     * @return the lamdas
     */
    public double[] getLamdas() {
        return lamdas;
    }

    /**
     * @param lamdas the lamdas to set
     */
    public void setLamdas(double[] lamdas) {
        this.lamdas = lamdas;
    }

    /**
     * @return the cs
     */
    public double[] getCs() {
        return cs;
    }

    /**
     * @param cs the cs to set
     */
    public void setCs(double[] cs) {
        this.cs = cs;
    }

    /**
     * @return the mus
     */
    public double[] getMus() {
        return mus;
    }

    /**
     * @param mus the mus to set
     */
    public void setMus(double[] mus) {
        this.mus = mus;
    }

    /**
     * @return the sigmas
     */
    public double[] getSigmas() {
        return sigmas;
    }

    /**
     * @param sigmas the sigmas to set
     */
    public void setSigmas(double[] sigmas) {
        this.sigmas = sigmas;
    }

    /**
     * @return the gammas
     */
    public double[] getGammas() {
        return gammas;
    }

    /**
     * @param gammas the gammas to set
     */
    public void setGammas(double[] gammas) {
        this.gammas = gammas;
    }

    /**
     * @return the rsEqual
     */
    public boolean isRsEqual() {
        return rsEqual;
    }

    /**
     * @param rsEqual the rsCombined to set
     */
    public void setRsEqual(boolean rsEqual) {
        this.rsEqual = rsEqual;
    }

    /**
     * @return the rSigmas
     */
    public double[] getRSigmas() {
        return rSigmas;
    }

    /**
     * @param rSigmas the rSigmas to set
     */
    public void setRSigmas(double[] rSigmas) {
        this.rSigmas = rSigmas;
    }

    /**
     * @return the rPis
     */
    public double[] getRPis() {
        return rPis;
    }

    /**
     * @param rPis the rPis to set
     */
    public void setRPis(double[] rPis) {
        this.rPis = rPis;
    }

    /**
     * @return the ns
     */
    public int[] getNs() {
        return Ns;
    }

    /**
     * @param ns the ns to set
     */
    public void setNs(int[] ns) {
        Ns = ns;
    }

    /**
     * @return the iotas
     */
    public boolean[] getIotas() {
        return iotas;
    }

    /**
     * @param iotas the iotas to set
     */
    public void setIotas(boolean[] iotas) {
        this.iotas = iotas;
    }

    /**
     * @return the phis
     */
    public double[] getPhis() {
        return phis;
    }

    /**
     * @param phis the phis to set
     */
    public void setPhis(double[] phis) {
        this.phis = phis;
    }

    /**
     * @return the zeta
     */
    public int getZeta() {
        return zeta;
    }

    /**
     * @param zeta the zeta to set
     */
    public void setZeta(int zeta) {
        this.zeta = zeta;
    }

    /**
     * @return the epsilon
     */
    public int getEpsilon() {
        return epsilon;
    }

    /**
     * @param epsilon the epsilon to set
     */
    public void setEpsilon(int epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * @return the taus
     */
    public int[] getTaus() {
        return taus;
    }

    /**
     * @param taus the taus to set
     */
    public void setTaus(int[] taus) {
        this.taus = taus;
    }

    /**
     * @return the simsPerParameterCombination
     */
    public int getSimsPerParameterCombination() {
        return simsPerParameterCombination;
    }

    /**
     * @param simsPerParameterCombination the simsPerParameterCombination to set
     */
    public void setSimsPerParameterCombination(int simsPerParameterCombination) {
        this.simsPerParameterCombination = simsPerParameterCombination;
    }

}

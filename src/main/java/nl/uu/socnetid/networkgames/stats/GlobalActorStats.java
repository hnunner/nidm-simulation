package nl.uu.socnetid.networkgames.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalActorStats {

    private int actorsOverall = 0;
    private int susceptibles = 0;
    private int infected = 0;
    private int recovered = 0;
    private int riskAverse = 0;
    private int riskNeutrals = 0;
    private int riskSeeking = 0;
    private double avRisk = 0.0;


    /**
     * @return the actorsOverall
     */
    public int getActorsOverall() {
        return actorsOverall;
    }

    /**
     * @param actorsOverall the actorsOverall to set
     */
    public void setActorsOverall(int actorsOverall) {
        this.actorsOverall = actorsOverall;
    }

    /**
     * Increases the amount of overall actors by one.
     */
    public void incActorsOverall() {
        this.actorsOverall++;
    }

    /**
     * Decreases the amount of overall actors by one.
     */
    public void decActorsOverall() {
        this.actorsOverall--;
    }

    /**
     * @return the susceptibles
     */
    public int getSusceptibles() {
        return susceptibles;
    }

    /**
     * @param susceptibles the susceptibles to set
     */
    public void setSusceptibles(int susceptibles) {
        this.susceptibles = susceptibles;
    }

    /**
     * Increases the amount of susceptibles by one.
     */
    public void incSusceptibles() {
        this.susceptibles++;
    }

    /**
     * Decreases the amount of susceptibles by one.
     */
    public void decSusceptibles() {
        this.susceptibles--;
    }

    /**
     * @return the infected
     */
    public int getInfected() {
        return infected;
    }

    /**
     * @param infected the infected to set
     */
    public void setInfected(int infected) {
        this.infected = infected;
    }

    /**
     * Increases the amount of infected by one.
     */
    public void incInfected() {
        this.infected++;
    }

    /**
     * Decreases the amount of infected by one.
     */
    public void decInfected() {
        this.infected--;
    }

    /**
     * @return the recovered
     */
    public int getRecovered() {
        return recovered;
    }

    /**
     * @param recovered the recovered to set
     */
    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    /**
     * Increases the amount of recovered by one.
     */
    public void incRecovered() {
        this.recovered++;
    }

    /**
     * Decreases the amount of recovered by one.
     */
    public void decRecovered() {
        this.recovered--;
    }

    /**
     * @return the riskAverse
     */
    public int getRiskAverse() {
        return riskAverse;
    }

    /**
     * @param riskAverse the riskAverse to set
     */
    public void setRiskAverse(int riskAverse) {
        this.riskAverse = riskAverse;
    }

    /**
     * Increases the amount of risk averse by one.
     */
    public void incRiskAverse() {
        this.riskAverse++;
    }

    /**
     * Decreases the amount of risk averse by one.
     */
    public void decRiskAverse() {
        this.riskAverse--;
    }

    /**
     * @return the riskNeutrals
     */
    public int getRiskNeutrals() {
        return riskNeutrals;
    }

    /**
     * @param riskNeutrals the riskNeutrals to set
     */
    public void setRiskNeutrals(int riskNeutrals) {
        this.riskNeutrals = riskNeutrals;
    }

    /**
     * Increases the amount of risk neutrals by one.
     */
    public void incRiskNeutrals() {
        this.riskNeutrals++;
    }

    /**
     * Decreases the amount of risk neutrals by one.
     */
    public void decRiskNeutrals() {
        this.riskNeutrals--;
    }

    /**
     * @return the riskSeeking
     */
    public int getRiskSeeking() {
        return riskSeeking;
    }

    /**
     * @param riskSeeking the riskSeeking to set
     */
    public void setRiskSeeking(int riskSeeking) {
        this.riskSeeking = riskSeeking;
    }

    /**
     * Increases the amount of risk seeking by one.
     */
    public void incRiskSeeking() {
        this.riskSeeking++;
    }

    /**
     * Decreases the amount of risk seeking by one.
     */
    public void decRiskSeeking() {
        this.riskSeeking--;
    }

    /**
     * @return the avRisk
     */
    public double getAvRisk() {
        return avRisk;
    }

    /**
     * @param avRisk the avRisk to set
     */
    public void setAvRisk(double avRisk) {
        this.avRisk = avRisk;
    }

}

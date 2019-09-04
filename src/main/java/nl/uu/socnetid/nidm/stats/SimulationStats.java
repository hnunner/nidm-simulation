package nl.uu.socnetid.nidm.stats;

import nl.uu.socnetid.nidm.simulation.SimulationStage;

/**
 *
 * @author Hendrik Nunner
 */
public class SimulationStats {

    private int upc = 0;
    private String uid;
    private int simPerUpc;
    private int roundStartInfection;
    private int roundLastInfection;
    private SimulationStage simStage;
    private int rounds;


    /**
     * @return the roundStartInfection
     */
    public int getRoundStartInfection() {
        return roundStartInfection;
    }
    /**
     * @param roundStartInfection the roundStartInfection to set
     */
    public void setRoundStartInfection(int roundStartInfection) {
        this.roundStartInfection = roundStartInfection;
    }
    /**
     * @return the roundLastInfection
     */
    public int getRoundLastInfection() {
        return roundLastInfection;
    }
    /**
     * @param roundLastInfection the roundLastInfection to set
     */
    public void setRoundLastInfection(int roundLastInfection) {
        this.roundLastInfection = roundLastInfection;
    }
    /**
     * @return the simStage
     */
    public SimulationStage getSimStage() {
        return simStage;
    }
    /**
     * @param simStage the simStage to set
     */
    public void setSimStage(SimulationStage simStage) {
        this.simStage = simStage;
    }
    /**
     * @return the rounds
     */
    public int getRounds() {
        return rounds;
    }
    /**
     * @param rounds the rounds to set
     */
    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    /**
     * @return the upc
     */
    public int getUpc() {
        return upc;
    }

    /**
     * @param upc the upc to set
     */
    public void setUpc(int upc) {
        this.upc = upc;
    }

    /**
     * increases the unique parameter combination
     */
    public void incUpc() {
        this.upc += 1;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the simPerUpc
     */
    public int getSimPerUpc() {
        return simPerUpc;
    }

    /**
     * @param simPerUpc the simPerUpc to set
     */
    public void setSimPerUpc(int simPerUpc) {
        this.simPerUpc = simPerUpc;
    }

    /**
     * increases the simPerUpc
     */
    public void incSimPerUpc() {
        ++this.simPerUpc;
    }

}

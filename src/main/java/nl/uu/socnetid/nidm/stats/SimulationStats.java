/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.stats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.uu.socnetid.nidm.simulation.SimulationStage;

/**
 *
 * @author Hendrik Nunner
 */
public class SimulationStats {

    private String uid = null;
    private int upcs = 0;
    private int upc = 0;
    private int simPerUpc;
    private int simIt = 1;
    private int roundStartInfection;
    private int roundLastInfection;
    private int epidemicDurationStatic;
    private int epidemicDurationDynamic;
    private int epidemicPeakSizeStatic;
    private int epidemicPeakSizeDynamic;
    private int epidemicPeakStatic;
    private int epidemicPeakDynamic;
    private SimulationStage simStage;
    private int rounds;
    private int currRound = 0;

    private int shotsGiven;
    private int agentsImmunized;
    private Map<String, Integer> professionsReceivedShots;


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
     * @return the upcs
     */
    public int getUpcs() {
        return upcs;
    }

    /**
     * @param upcs the upcs to set
     */
    public void setUpcs(int upcs) {
        this.upcs = upcs;
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
        if (this.uid == null) {
            return this.getUpc() + "#" + this.getSimPerUpc() + "#" + this.getSimIt();
        }
        return this.uid;
    }

    /**
     * @param uid
     *          the uid to set
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

    /**
     * @return the simIt
     */
    public int getSimIt() {
        return simIt;
    }

    /**
     * @param simIt the simIt to set
     */
    public void setSimIt(int simIt) {
        this.simIt = simIt;
    }

    /**
     * increases the simIt
     */
    public void incSimIt() {
        ++this.simIt;
    }

    /**
     * @return the currRound
     */
    public int getCurrRound() {
        return currRound;
    }

    /**
     * increases the currRound
     */
    public void incCurrRound() {
        ++this.currRound;
    }

    /**
     * increases the currRound
     */
    public void resetCurrRound() {
        this.currRound = 0;
    }

    /**
     * @return the epidemicDurationStatic
     */
    public int getEpidemicDurationStatic() {
        return epidemicDurationStatic;
    }

    /**
     * @param epidemicDurationStatic the epidemicDurationStatic to set
     */
    public void setEpidemicDurationStatic(int epidemicDurationStatic) {
        this.epidemicDurationStatic = epidemicDurationStatic;
    }

    /**
     * @return the epidemicDurationDynamic
     */
    public int getEpidemicDurationDynamic() {
        return epidemicDurationDynamic;
    }

    /**
     * @param epidemicDurationDynamic the epidemicDurationDynamic to set
     */
    public void setEpidemicDurationDynamic(int epidemicDurationDynamic) {
        this.epidemicDurationDynamic = epidemicDurationDynamic;
    }

    /**
     * @return the epidemicPeakSizeStatic
     */
    public int getEpidemicPeakSizeStatic() {
        return epidemicPeakSizeStatic;
    }

    /**
     * @param epidemicPeakSizeStatic the epidemicPeakSizeStatic to set
     */
    public void setEpidemicPeakSizeStatic(int epidemicPeakSizeStatic) {
        this.epidemicPeakSizeStatic = epidemicPeakSizeStatic;
    }

    /**
     * @return the epidemicPeakSizeDynamic
     */
    public int getEpidemicPeakSizeDynamic() {
        return epidemicPeakSizeDynamic;
    }

    /**
     * @param epidemicPeakSizeDynamic the epidemicPeakSizeDynamic to set
     */
    public void setEpidemicMaxInfectionsDynamic(int epidemicPeakSizeDynamic) {
        this.epidemicPeakSizeDynamic = epidemicPeakSizeDynamic;
    }

    /**
     * @return the epidemicPeakStatic
     */
    public int getEpidemicPeakStatic() {
        return epidemicPeakStatic;
    }

    /**
     * @param epidemicPeakStatic the epidemicPeakStatic to set
     */
    public void setEpidemicPeakStatic(int epidemicPeakStatic) {
        this.epidemicPeakStatic = epidemicPeakStatic;
    }

    /**
     * @return the epidemicPeakDynamic
     */
    public int getEpidemicPeakDynamic() {
        return epidemicPeakDynamic;
    }

    /**
     * @param epidemicPeakDynamic the epidemicPeakDynamic to set
     */
    public void setEpidemicPeakDynamic(int epidemicPeakDynamic) {
        this.epidemicPeakDynamic = epidemicPeakDynamic;
    }

    /**
     * @return the shotsGiven
     */
    public int getShotsGiven() {
        return shotsGiven;
    }

    /**
     * @param shotsGiven the shotsGiven to set
     */
    public void setShotsGiven(int shotsGiven) {
        this.shotsGiven = shotsGiven;
    }

    /**
     * @return the agentsImmunized
     */
    public int getAgentsImmunized() {
        return agentsImmunized;
    }

    /**
     * @param agentsImmunized the agentsImmunized to set
     */
    public void setAgentsImmunized(int agentsImmunized) {
        this.agentsImmunized = agentsImmunized;
    }

    /**
     * @return the professionsReceivedShots
     */
    public String getProfessionsReceivedShots() {
        StringBuilder sb = new StringBuilder();

        if (professionsReceivedShots != null) {
            Iterator<String> professions = this.professionsReceivedShots.keySet().iterator();
            while (professions.hasNext()) {
                String profession = professions.next();
                if (sb.toString().length() > 0) {
                    sb.append(",");
                }
                sb.append(profession).append("_").append(this.professionsReceivedShots.get(profession));
            }
        }

        return sb.toString();
    }

    /**
     * @param professionsReceivedShots the professionsReceivedShots to set
     */
    public void setProfessionsReceivedShots(Map<String, Integer> professionsReceivedShots) {
        this.professionsReceivedShots = professionsReceivedShots;
    }

    /**
     * Resets the epidemic stats (duration, peak, max infections)
     */
    public void resetEpidemicStats() {
        this.epidemicDurationDynamic = 0;
        this.epidemicDurationStatic = 0;
        this.epidemicPeakSizeDynamic = 0;
        this.epidemicPeakSizeStatic = 0;
        this.epidemicPeakDynamic = 0;
        this.epidemicPeakStatic = 0;
        this.shotsGiven = 0;
        this.professionsReceivedShots = new HashMap<String, Integer>();
    }

}

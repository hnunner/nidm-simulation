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
    private int currRound = 0;


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

}

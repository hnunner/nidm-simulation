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
package nl.uu.socnetid.nidm.utility;

/**
 * @author Hendrik Nunner
 */
public class Utility {

    // overall utility
    private double overallUtility;

    // benefits
    private double benefitDirectConnections;
    private double benefitIndirectConnections;

    // costs
    private double costsDirectConnections;
    private double effectOfDisease;

    /**
     * Constructor.
     *
     * @param benefitDirectConnections
     *          the benefit of direct connections
     * @param benefitIndirectConnections
     *          the benefit of indirect connections
     * @param costsDirectConnections
     *          the costs of direct connections
     * @param effectOfDisease
     *          the effect of the disease
     */
    public Utility(double benefitDirectConnections, double benefitIndirectConnections,
            double costsDirectConnections, double effectOfDisease) {

        this.benefitDirectConnections = benefitDirectConnections;
        this.benefitIndirectConnections = benefitIndirectConnections;
        this.costsDirectConnections = costsDirectConnections;
        this.effectOfDisease = effectOfDisease;

        this.overallUtility = benefitDirectConnections
                + benefitIndirectConnections
                - costsDirectConnections
                - effectOfDisease;
    }

    /**
     * @return the overallUtility
     */
    public double getOverallUtility() {
        return overallUtility;
    }

    /**
     * @return the benefitDirectConnections
     */
    public double getBenefitDirectConnections() {
        return benefitDirectConnections;
    }

    /**
     * @return the benefitIndirectConnections
     */
    public double getBenefitIndirectConnections() {
        return benefitIndirectConnections;
    }

    /**
     * @return the costsDirectConnections
     */
    public double getCostsDirectConnections() {
        return costsDirectConnections;
    }

    /**
     * @return the effectOfDisease
     */
    public double getEffectOfDisease() {
        return effectOfDisease;
    }

}

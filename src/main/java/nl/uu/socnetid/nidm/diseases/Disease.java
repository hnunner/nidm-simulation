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
package nl.uu.socnetid.nidm.diseases;

/**
 * @author Hendrik Nunner
 */
public interface Disease {

    /**
     * Evolves the disease: increases the duration counter and
     * checks whether the disease is being defeated already.
     */
    void evolve();

    /**
     * Checks whether the disease is infectious.
     *
     * @return true if the disease is infectious, false otherwise
     */
    boolean isInfectious();

    /**
     * Checks whether the disease is cured.
     *
     * @return true if the disease is cured, false otherwise
     */
    boolean isCured();

    /**
     * Gets the time remaining until the disease is cured.
     *
     * @return the time in round until the disease is cured
     */
    int getTimeUntilCured();

    /**
     * Gets the {@link DiseaseSpecs}.
     *
     * @return the {@link DiseaseSpecs}
     */
    DiseaseSpecs getDiseaseSpecs();

}

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
package nl.uu.socnetid.nidm.agents;

/**
 * @author Hendrik Nunner
 */
public enum AgentAttributes {

    UTILITY_FUNCTION("utility.function"),
    DISEASE_SPECS("disease.specs"),
    DISEASE_GROUP("disease.group"),
    DISEASE_INFECTION("disease.infection"),
    UI_CLASS("ui.class"),
    RISK_FACTOR_SIGMA("risk.factor.sigma"),
    RISK_FACTOR_PI("risk.factor.pi"),
    RISK_MEANING_SIGMA("risk.meaning.sigma"),
    RISK_MEANING_PI("risk.meaning.pi"),
    PHI("phi"),
    PSI("psi"),
    XI("xi"),
    OMEGA("omega"),
    OMEGA_SHUFFLE("omega.shuffle"),
    SATISFIED("satisfied"),
    CONNECTION_STATS("connection.stats"),
    AGE("age"),
    ASSORTATIVITY_CONDITION("assortativity.condition"),
    CONSIDER_AGE("consider.age"),
    FORCE_INFECTED("force.infected"),
    INITIAL_INDEX_CASE_DISTANCE("initial.index.case.distance"),
    BETWEENNESS("betweenness"),
    BETWEENNESS_LAST_COMPUTATION("betweenness.last.computation"),
    CLOSENESS("closeness"),
    CLOSENESS_LAST_COMPUTATION("closeness.last.computation"),
    CLUSTERING("clustering"),
    CLUSTERING_LAST_COMPUTATION("clustering.last.computation"),
    ASSORTATIVITY("assortativity"),
    ASSORTATIVITY_LAST_COMPUTATION("assortativity.last.computation"),
    INDEX_CASE_DISTANCE("index.case.distance"),
    INDEX_CASE_DISTANCE_LAST_COMPUTATION("index.case.distance.last.computation");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    AgentAttributes(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

}

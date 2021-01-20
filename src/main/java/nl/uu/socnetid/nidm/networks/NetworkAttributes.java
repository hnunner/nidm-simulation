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
package nl.uu.socnetid.nidm.networks;

/**
 * @author Hendrik Nunner
 */
public enum NetworkAttributes {

    ARRANGE_IN_CIRCLE("arrange.in.circle"),

    TIMESTEPS_STABLE("timesteps.stable"),
    STABLE("stable"),

    AV_PATH_LENGTH_ROUND_LAST_COMPUTATION("av.path.length.round.last.computation"),
    AV_PATH_LENGTH("av.path.length"),
    AV_CLUSTERING_ROUND_LAST_COMPUTATION("av.clustering.round.last.computation"),
    AV_CLUSTERING("av.clustering"),
    AV_DEGREE_ROUND_LAST_COMPUTATION("av.degree.round.last.computation"),
    AV_DEGREE("av.degree"),
    AV_DEGREE_2("av.degree.2"),
    AV_DEGREE_THEORETIC("av.degree.theoretic"),
    AV_DEGREE_SATISFIED("av.degree.satisfied"),
    AV_DEGREE_UNSATISFIED("av.degree.unsatisfied"),
    AV_BETWEENNESS_ROUND_LAST_COMPUTATION("av.betweenness.round.last.computation"),
    AV_BETWEENNESS("av.betweenness"),
    AV_CLOSENESS_ROUND_LAST_COMPUTATION("av.closeness.round.last.computation"),
    AV_CLOSENESS("av.closeness"),

    AV_UTILITY("av.utility"),
    AV_SOCIAL_BENEFITS("av.social.benefits"),
    AV_SOCIAL_COSTS("av.social.costs"),
    AV_DISEASE_COSTS("av.disease.costs"),
    N("N"),
    HAS_ACTIVE_INFECTION("has.active.infection"),
    EMPTY("empty"),
    FULL("full"),
    RING("ring"),
    STAR("star"),

    MAX_R_PI("max.r.pi"),
    MAX_R_SIGMA("max.r.sigma"),
    MAX_AGE("max.age"),

    ASSORTATIVITY_CONDITIONS("assortativity.conditions"),
    ASSORTATIVITY_ROUND_LAST_COMPUTATION("assortativity.round.last.computation"),
    ASSORTATIVITY_RISK_PERCEPTION("assortativity.risk.perception"),
    ASSORTATIVITY_AGE("assortativity.age"),
    ASSORTATIVITY_PROFESSION("assortativity.profession");


    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    NetworkAttributes(String name) {
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

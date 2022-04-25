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

import java.util.Set;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public abstract class UtilityFunction {

    protected static final String STRING_DELIMITER = ";";
    protected static final String UF_TYPE = "type:";
    protected static final String TYPE_BURGER_BUSKENS = "BB";
    protected static final String TYPE_CARAYOL_ROUX = "CR";
    protected static final String TYPE_CIDM = "CIDM";
    protected static final String TYPE_CUMULATIVE = "CUM";
    protected static final String TYPE_IRTC = "IRTC";
    protected static final String TYPE_NUNNER_BUSKENS = "NB";
    protected static final String TYPE_NUNNER_BUSKENS_2 = "NB2";
    protected static final String TYPE_TRUNCATED_CONNECTIONS = "TCM";
    
    private double overestimate = 1.0;


    /**
     * Computes the utility for a agent.
     *
     * @param agent
     *          the agent to compute the utility for
     * @return the agent's utility based on the connections
     */
    public Utility getUtility(Agent agent) {
        return this.getUtility(agent, null, null);
    }

    /**
     * Computes the utility for a agent including another agent as direct connection.
     *
     * @param agent
     *          the agent to compute the utility for
     * @param withs
     *          the agents to include as direct connections
     * @return the agent's utility based on the connections
     */
    public Utility getUtilityWith(Agent agent, Set<Agent> withs) {
        return this.getUtility(agent, withs, null);
    }

    /**
     * Computes the utility for a agent excluding another agent as direct connection.
     *
     * @param agent
     *          the agent to compute the utility for
     * @param withouts
     *          the agents to exclude as direct connections
     * @return the agent's utility based on the connections
     */
    public Utility getUtilityWithout(Agent agent, Set<Agent> withouts) {
        return this.getUtility(agent, null, withouts);
    }

    /**
     * Computes the utility for a agent including another agent as direct connection and
     * excluding another agent as direct connection.
     *
     * @param agent
     *          the agent to compute the utility for
     * @param withs
     *          the agents to include as direct connections
     * @param withouts
     *          the agents to exclude as direct connections
     * @return the agent's utility based on the connections
     */
    public Utility getUtility(Agent agent, Set<Agent> withs, Set<Agent> withouts) {

        LocalAgentConnectionsStats lacs = StatsComputer.computeLocalAgentConnectionsStats(agent, withs, withouts);

        return new Utility(
                getSocialBenefits(lacs, agent),
                getSocialCosts(lacs, agent),
                getDiseaseCosts(lacs, agent));
    }

    /**
     * @return the name of the utility function to be used in the stats window
     */
    public abstract String getStatsName();

    /**
     * Computes the benefits of social connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the benefits for
     * @return the benefit of direct connections
     */
    protected abstract double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * Computes the costs of social connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the costs for
     * @return the costs of direct connections
     */
    protected abstract double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * Gets the theoretic degree, dependent on the parameter settings.
     *
     * @return the theoretic degree
     */
    public abstract double getTheoreticDegree();

    /**
     * Computes the costs of a disease.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the effect for
     * @return the benefit of a disease
     */
    protected double getDiseaseCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        int nI = lacs.getnI();
        double p;
        double s;
        double rSigma = agent.getRSigma();
        double rPi = agent.getRPi();
        
        // depending own agent's own risk group
        switch (agent.getDiseaseGroup()) {
            case SUSCEPTIBLE:
                p = Math.pow(StatsComputer.computeProbabilityOfInfection(agent, nI), (2 - rPi)) * this.overestimate;
                s = Math.pow(agent.getDiseaseSpecs().getSigma(), rSigma) * this.overestimate;
                break;

            case INFECTED:
                p = 1;
                s = agent.getDiseaseSpecs().getSigma();
                break;

            case RECOVERED:
            case VACCINATED:
                p = 0;
                s = 0;
                break;

            default:
                throw new RuntimeException("Unknown disease group: " + agent.getDiseaseGroup());
        }
        
        return p * s;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(UF_TYPE).append(this.getStatsName()).append(STRING_DELIMITER);
        sb.append(this.getUtilityFunctionDetails());
        return sb.toString();
    }

    /**
     * Gets a string representation of the utility function's details.
     *
     * @return a string representation of the utility function's details
     */
    protected abstract String getUtilityFunctionDetails();

    /**
     * Factory method to create a utility function from its String representation.
     *
     * @param ufString
     *          utility function string
     * @return the utility function
     */
    public static UtilityFunction fromString(String ufString) {

        UtilityFunction uf = null;

        String[] ufSplit = ufString.split(STRING_DELIMITER);
        for (String value : ufSplit) {
            if (value.contains(UF_TYPE)) {
                String type = value.replace(UF_TYPE, "");
                if (type.equals(TYPE_BURGER_BUSKENS)) {
                    uf = new BurgerBuskens(ufSplit);
                    break;
                } else if (type.equals(TYPE_CARAYOL_ROUX)) {
                    uf = new CarayolRoux(ufSplit);
                    break;
                } else if (type.equals(TYPE_CIDM)) {
                    uf = new Cidm(ufSplit);
                    break;
                } else if (type.equals(TYPE_CUMULATIVE)) {
                    uf = new Cumulative(ufSplit);
                    break;
                } else if (type.equals(TYPE_IRTC)) {
                    uf = new Irtc(ufSplit);
                    break;
                } else if (type.equals(TYPE_NUNNER_BUSKENS)) {
                    uf = new NunnerBuskens(ufSplit);
                    break;
                } else if (type.equals(TYPE_NUNNER_BUSKENS_2)) {
                    uf = new NunnerBuskens2(ufSplit);
                    break;
                } else if (type.equals(TYPE_TRUNCATED_CONNECTIONS)) {
                    uf = new TruncatedConnections(ufSplit);
                    break;
                } else {
                    throw new IllegalArgumentException("Unknown utility function type: " + type);
                }
            }
        }

        return uf;
    }

    protected void setOverestimate(double overestimate) {
    	this.overestimate = overestimate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals()
     */
    @Override
    public abstract boolean equals(Object o);

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public abstract int hashCode();

}

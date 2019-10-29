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
package nl.uu.socnetid.nidm.data;

import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public enum LogValues {

    // PARAMETERS (independent variables)
    // simulation
    IV_SIM_UID("sim.uid"),
    IV_SIM_UPC("sim.upc"),
    IV_SIM_CNT("sim.cnt"),
    IV_SIM_ROUND("sim.round"),
    // agent
    IV_AGENT_ID("agent.id"),
    // Cidm
    IV_CIDM_ALPHA("cidm.alpha"),
    IV_CIDM_ALPHA_AV("cidm.alpha.av"),
    IV_CIDM_KAPPA("cidm.kappa"),
    IV_CIDM_KAPPA_AV("cidm.kappa.av"),
    IV_CIDM_BETA("cidm.beta"),
    IV_CIDM_BETA_AV("cidm.beta.av"),
    IV_CIDM_LAMDA("cidm.lamda"),
    IV_CIDM_LAMDA_AV("cidm.lamda.av"),
    IV_CIDM_C("cidm.c"),
    IV_CIDM_C_AV("cidm.c.av"),
    IV_CIDM_MU("cidm.mu"),
    IV_CIDM_MU_AV("cidm.mu.av"),
    IV_CIDM_SIGMA("cidm.sigma"),
    IV_CIDM_SIGMA_AV("cidm.sigma.av"),
    IV_CIDM_GAMMA("cidm.gamma"),
    IV_CIDM_GAMMA_AV("cidm.gamma.av"),
    IV_CIDM_RS_EQUAL("cidm.rs.equal"),
    IV_CIDM_R_SIGMA("cidm.r.sigma"),
    IV_CIDM_R_SIGMA_AV("cidm.r.sigma.av"),
    IV_CIDM_R_PI("cidm.r.pi"),
    IV_CIDM_R_PI_AV("cidm.r.pi.av"),
    IV_CIDM_NET_SIZE_RANDOM("cidm.N.random"),
    IV_CIDM_NET_SIZE_RANDOM_MIN("cidm.N.random.min"),
    IV_CIDM_NET_SIZE_RANDOM_MAX("cidm.N.random.max"),
    IV_CIDM_NET_SIZE("cidm.N"),
    IV_CIDM_IOTA_RANDOM("cidm.iota.random"),
    IV_CIDM_IOTA_RANDOM_MIN("cidm.iota.random.min"),
    IV_CIDM_IOTA_RANDOM_MAX("cidm.iota.random.max"),
    IV_CIDM_IOTA("cidm.iota"),
    IV_CIDM_PHI_RANDOM("cidm.phi.random"),
    IV_CIDM_PHI_RANDOM_MIN("cidm.phi.random.min"),
    IV_CIDM_PHI_RANDOM_MAX("cidm.phi.random.max"),
    IV_CIDM_PHI("cidm.phi"),
    IV_CIDM_PHI_AV("cidm.phi.av"),
    IV_CIDM_TAU("cidm.tau"),
    IV_CIDM_TAU_AV("cidm.tau.av"),
    IV_CIDM_ZETA("cidm.zeta"),
    IV_CIDM_EPSILON("cidm.epsilon"),
    IV_CIDM_SIMS_PER_PC("cidm.n"),
    // BurgerBuskens
    IV_BB_B1_RANDOM("bb.b1.random"),
    IV_BB_B1_RANDOM_MIN("bb.b1.random.min"),
    IV_BB_B1_RANDOM_MAX("bb.b1.random.max"),
    IV_BB_B1("bb.b1"),
    IV_BB_C1_RANDOM("bb.c1.random"),
    IV_BB_C1_RANDOM_MIN("bb.c1.random.min"),
    IV_BB_C1_RANDOM_MAX("bb.c1.random.max"),
    IV_BB_C1("bb.c1"),
    IV_BB_C2_RANDOM("bb.c2.random"),
    IV_BB_C2_RANDOM_MIN("bb.c2.random.min"),
    IV_BB_C2_RANDOM_MAX("bb.c2.random.max"),
    IV_BB_C2("bb.c2"),
    IV_BB_B2_RANDOM("bb.b2.random"),
    IV_BB_B2_RANDOM_MIN("bb.b2.random.min"),
    IV_BB_B2_RANDOM_MAX("bb.b2.random.max"),
    IV_BB_B2("bb.b2"),
    IV_BB_C3_RANDOM("bb.c3.random"),
    IV_BB_C3_RANDOM_MIN("bb.c3.random.min"),
    IV_BB_C3_RANDOM_MAX("bb.c3.random.max"),
    IV_BB_C3("bb.c3"),
    IV_BB_NET_SIZE_RANDOM("bb.N.random"),
    IV_BB_NET_SIZE_RANDOM_MIN("bb.N.random.min"),
    IV_BB_NET_SIZE_RANDOM_MAX("bb.N.random.max"),
    IV_BB_NET_SIZE("bb.N"),
    IV_BB_IOTA_RANDOM("bb.iota.random"),
    IV_BB_IOTA_RANDOM_MIN("bb.iota.random.min"),
    IV_BB_IOTA_RANDOM_MAX("bb.iota.random.max"),
    IV_BB_IOTA("bb.iota"),
    IV_BB_PHI_RANDOM("bb.phi.random"),
    IV_BB_PHI_RANDOM_MIN("bb.phi.random.min"),
    IV_BB_PHI_RANDOM_MAX("bb.phi.random.max"),
    IV_BB_PHI("bb.phi"),
    IV_BB_SIMS_PER_PC("bb.n"),
    // CarayolRoux
    IV_CR_OMEGA_RANDOM("cr.omega.random"),
    IV_CR_OMEGA_RANDOM_MIN("cr.omega.random.min"),
    IV_CR_OMEGA_RANDOM_MAX("cr.omega.random.max"),
    IV_CR_OMEGA("cr.omega"),
    IV_CR_DELTA_RANDOM("cr.delta.random"),
    IV_CR_DELTA_RANDOM_MIN("cr.delta.random.min"),
    IV_CR_DELTA_RANDOM_MAX("cr.delta.random.max"),
    IV_CR_DELTA("cr.delta"),
    IV_CR_C_RANDOM("cr.c.random"),
    IV_CR_C_RANDOM_MIN("cr.c.random.min"),
    IV_CR_C_RANDOM_MAX("cr.c.random.max"),
    IV_CR_C("cr.c"),
    IV_CR_NET_SIZE_RANDOM("cr.N.random"),
    IV_CR_NET_SIZE_RANDOM_MIN("cr.N.random.min"),
    IV_CR_NET_SIZE_RANDOM_MAX("cr.N.random.max"),
    IV_CR_NET_SIZE("cr.N"),
    IV_CR_IOTA_RANDOM("cr.iota.random"),
    IV_CR_IOTA_RANDOM_MIN("cr.iota.random.min"),
    IV_CR_IOTA_RANDOM_MAX("cr.iota.random.max"),
    IV_CR_IOTA("cr.iota"),
    IV_CR_PHI_RANDOM("cr.phi.random"),
    IV_CR_PHI_RANDOM_MIN("cr.phi.random.min"),
    IV_CR_PHI_RANDOM_MAX("cr.phi.random.max"),
    IV_CR_PHI("cr.phi"),
    IV_CR_SIMS_PER_PC("cr.n"),
    // NunnerBuskens
    IV_NB_B1_RANDOM("nb.b1.random"),
    IV_NB_B1_RANDOM_MIN("nb.b1.random.min"),
    IV_NB_B1_RANDOM_MAX("nb.b1.random.max"),
    IV_NB_B1("nb.b1"),
    IV_NB_C1_RANDOM("nb.c1.random"),
    IV_NB_C1_RANDOM_MIN("nb.c1.random.min"),
    IV_NB_C1_RANDOM_MAX("nb.c1.random.max"),
    IV_NB_C1("nb.c1"),
    IV_NB_C2_RANDOM("nb.c2.random"),
    IV_NB_C2_RANDOM_MIN("nb.c2.random.min"),
    IV_NB_C2_RANDOM_MAX("nb.c2.random.max"),
    IV_NB_C2("nb.c2"),
    IV_NB_B2_RANDOM("nb.b2.random"),
    IV_NB_B2_RANDOM_MIN("nb.b2.random.min"),
    IV_NB_B2_RANDOM_MAX("nb.b2.random.max"),
    IV_NB_B2("nb.b2"),
    IV_NB_ALPHA_RANDOM("nb.alpha.random"),
    IV_NB_ALPHA_RANDOM_MIN("nb.alpha.random.min"),
    IV_NB_ALPHA_RANDOM_MAX("nb.alpha.random.max"),
    IV_NB_ALPHA("nb.alpha"),
    IV_NB_NET_SIZE_RANDOM("nb.N.random"),
    IV_NB_NET_SIZE_RANDOM_MIN("nb.N.random.min"),
    IV_NB_NET_SIZE_RANDOM_MAX("nb.N.random.max"),
    IV_NB_NET_SIZE("nb.N"),
    IV_NB_IOTA_RANDOM("nb.iota.random"),
    IV_NB_IOTA_RANDOM_MIN("nb.iota.random.min"),
    IV_NB_IOTA_RANDOM_MAX("nb.iota.random.max"),
    IV_NB_IOTA("nb.iota"),
    IV_NB_PHI_RANDOM("nb.phi.random"),
    IV_NB_PHI_RANDOM_MIN("nb.phi.random.min"),
    IV_NB_PHI_RANDOM_MAX("nb.phi.random.max"),
    IV_NB_PHI("nb.phi"),
    IV_NB_YGLOBAL_RANDOM("nb.yglobal.random"),
    IV_NB_YGLOBAL("nb.yglobal"),
    IV_NB_SIMS_PER_PC("nb.n"),

    // PROPERTIES (dependent variables)
    // simulation
    DV_SIM_STAGE("sim.stage"),
    DV_SIM_EPIDEMIC_DURATION("sim.epidemic.duration"),
    // network
    DV_NET_PERCENTAGE_SUSCEPTIBLE("net.pct.sus"),
    DV_NET_PERCENTAGE_INFECTED("net.pct.inf"),
    DV_NET_PERCENTAGE_RECOVERED("net.pct.rec"),
    DV_NET_STABLE("net.stable"),
    DV_NET_DENSITY("net.density"),
    DV_NET_DENSITY_PRE("net.density.pre.epidemic"),
    DV_NET_DENSITY_POST("net.density.post.epidemic"),
    DV_NET_AV_DEGREE("net.degree.av"),
    DV_NET_AV_DEGREE_PRE("net.degree.pre.epidemic.av"),
    DV_NET_AV_DEGREE_POST("net.degree.post.epidemic.av"),
    DV_NET_AV_DEGREE2_PRE("net.degree2.pre.epidemic.av"),
    DV_NET_AV_DEGREE2_POST("net.degree2.post.epidemic.av"),
    DV_NET_AV_CLOSENESS_PRE("net.closeness.pre.epidemic.av"),
    DV_NET_AV_CLOSENESS_POST("net.closeness.post.epidemic.av"),
    DV_NET_AV_CLUSTERING("net.clustering.av"),
    DV_NET_AV_CLUSTERING_PRE("net.clustering.pre.epidemic.av"),
    DV_NET_AV_CLUSTERING_POST("net.clustering.post.epidemic.av"),
    DV_NET_AV_PATHLENGTH("net.pathlength.av"),
    DV_NET_AV_UTIL_PRE("net.utility.pre.epidemic.av"),
    DV_NET_AV_UTIL_POST("net.utility.post.epidemic.av"),
    DV_NET_AV_BENEFIT_SOCIAL_PRE("net.benefit.social.pre.epidemic.av"),
    DV_NET_AV_BENEFIT_SOCIAL_POST("net.benefit.social.post.epidemic.av"),
    DV_NET_AV_COSTS_SOCIAL_PRE("net.costs.social.pre.epidemic.av"),
    DV_NET_AV_COSTS_SOCIAL_POST("net.costs.social.post.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_PRE("net.costs.disease.pre.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_POST("net.costs.disease.post.epidemic.av"),
    DV_NET_TIES_BROKEN_EPIDEMIC("net.ties.broken.epidemic"),
    // agent
    DV_AGENT_SATISFIED("agent.satisfied"),
    DV_AGENT_UTIL("agent.util"),
    DV_AGENT_BENEFIT_SOCIAL("agent.benefit.social"),
    DV_AGENT_COSTS_SOCIAL("agent.costs.social"),
    DV_AGENT_COSTS_DISEASE("agent.costs.disease"),
    DV_AGENT_DISEASE_STATE("agent.dis.state"),
    DV_AGENT_DISEASE_ROUNDS_REMAINING("agent.dis.rounds.remaining"),
    DV_AGENT_DEGREE1("agent.degree"),
    DV_AGENT_DEGREE2("agent.degree2"),
    DV_AGENT_CLOSENESS("agent.closeness"),
    DV_AGENT_CLUSTERING("agent.clustering"),
    DV_AGENT_CONS_BROKEN_ACTIVE("agent.cons.broken.active"),
    DV_AGENT_CONS_BROKEN_PASSIVE("agent.cons.broken.passive"),
    DV_AGENT_CONS_OUT_ACCEPTED("agent.cons.out.accepted"),
    DV_AGENT_CONS_OUT_DECLINED("agent.cons.out.declined"),
    DV_AGENT_CONS_IN_ACCEPTED("agent.cons.in.accepted"),
    DV_AGENT_CONS_IN_DECLINED("agent.cons.in.declined"),
    // index
    DV_INDEX_SATISFIED("index.satisfied"),
    DV_INDEX_UTIL("index.util"),
    DV_INDEX_BENEFIT_DIST1("index.benefit.distance.1"),
    DV_INDEX_BENEFIT_DIST2("index.benefit.distance.2"),
    DV_INDEX_COSTS_DIST1("index.costs.distance.1"),
    DV_INDEX_COSTS_DISEASE("index.costs.disease"),
    DV_INDEX_DISEASE_STATE("index.dis.state"),
    DV_INDEX_DISEASE_ROUNDS_REMAINING("index.dis.rounds.remaining"),
    DV_INDEX_DEGREE1("index.degree"),
    DV_INDEX_DEGREE2("index.degree2"),
    DV_INDEX_CLOSENESS("index.closeness"),
    DV_INDEX_CLUSTERING("index.clustering"),
    DV_INDEX_CONS_BROKEN_ACTIVE("index.cons.broken.active"),
    DV_INDEX_CONS_BROKEN_PASSIVE("index.cons.broken.passive"),
    DV_INDEX_CONS_OUT_ACCEPTED("index.cons.out.accepted"),
    DV_INDEX_CONS_OUT_DECLINED("index.cons.out.declined"),
    DV_INDEX_CONS_IN_ACCEPTED("index.cons.in.accepted"),
    DV_INDEX_CONS_IN_DECLINED("index.cons.in.declined"),

    // OTHERS
    GEXF_FILE("gexf.filename");


    // dependent variables feasible for regression analysis
    private static final LogValues[] REG_DEPENDENTS = {
            DV_NET_DENSITY_PRE,
            // DV_NET_AV_DEGREE_PRE,
            // DV_NET_AV_DEGREE2_PRE,
            // DV_NET_AV_CLOSENESS_PRE,
            // DV_NET_AV_CLUSTERING_PRE,
            DV_INDEX_DEGREE1,
            // DV_INDEX_DEGREE2,
            // DV_INDEX_CLOSENESS,
            // DV_INDEX_CLUSTERING
            };

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    LogValues(String name) {
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

    /**
     * Gets the independent variables feasible for cidm regression analyses.
     *
     * @return the independent variables feasible for cidm regression analyses
     */
    public static List<LogValues> getRegressionIndependentVariablesCidm() {
        List<LogValues> regParams = new LinkedList<LogValues>();
        CidmParameters cidmParams = PropertiesHandler.getInstance().getCidmParameters();
        if (cidmParams.getAlphas().length > 1) regParams.add(IV_CIDM_ALPHA_AV);
        if (cidmParams.getKappas().length > 1) regParams.add(IV_CIDM_KAPPA_AV);
        if (cidmParams.getBetas().length > 1) regParams.add(IV_CIDM_BETA_AV);
        if (cidmParams.getLamdas().length > 1) regParams.add(IV_CIDM_LAMDA_AV);
        if (cidmParams.getCs().length > 1) regParams.add(IV_CIDM_C_AV);
        if (cidmParams.getMus().length > 1) regParams.add(IV_CIDM_MU_AV);
        if (cidmParams.getSigmas().length > 1) regParams.add(IV_CIDM_SIGMA_AV);
        if (cidmParams.getGammas().length > 1) regParams.add(IV_CIDM_GAMMA_AV);
        if (cidmParams.getRSigmas().length > 1) regParams.add(IV_CIDM_R_SIGMA_AV);
        if (!cidmParams.isRsEqual() && cidmParams.getRPis().length > 1) regParams.add(IV_CIDM_R_PI_AV);
        if (cidmParams.getNs().length > 1) regParams.add(IV_CIDM_NET_SIZE);
        if (cidmParams.getIotas().length > 1) regParams.add(IV_CIDM_IOTA);
        if (cidmParams.getPhis().length > 1) regParams.add(IV_CIDM_PHI_AV);
        if (cidmParams.getTaus().length > 1) regParams.add(IV_CIDM_TAU_AV);
        return regParams;
    }

    /**
     * Gets the dependent variables feasible for regression analyses.
     *
     * @return the dependent variablesfeasible for regression analyses
     */
    public static LogValues[] getRegressionDependentVariables() {
        return REG_DEPENDENTS;
    }

}

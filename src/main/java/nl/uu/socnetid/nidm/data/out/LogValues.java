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
package nl.uu.socnetid.nidm.data.out;

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
    IV_SIM_IT("sim.it"),
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
    IV_CIDM_OMEGA_RANDOM("cidm.omega.random"),
    IV_CIDM_OMEGA_RANDOM_MIN("cidm.omega.random.min"),
    IV_CIDM_OMEGA_RANDOM_MAX("cidm.omega.random.max"),
    IV_CIDM_OMEGA("cidm.omega"),
    IV_CIDM_OMEGA_AV("cidm.omega.av"),
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
    IV_BB_OMEGA_RANDOM("bb.omega.random"),
    IV_BB_OMEGA_RANDOM_MIN("bb.omega.random.min"),
    IV_BB_OMEGA_RANDOM_MAX("bb.omega.random.max"),
    IV_BB_OMEGA("bb.omega"),
    IV_BB_SIMS_PER_PC("bb.n"),
    // CarayolRoux
    IV_CR_CROMEGA_RANDOM("cr.cromega.random"),
    IV_CR_CROMEGA_RANDOM_MIN("cr.cromega.random.min"),
    IV_CR_CROMEGA_RANDOM_MAX("cr.cromega.random.max"),
    IV_CR_CROMEGA("cr.cromega"),
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
    IV_CR_OMEGA_RANDOM("cr.omega.random"),
    IV_CR_OMEGA_RANDOM_MIN("cr.omega.random.min"),
    IV_CR_OMEGA_RANDOM_MAX("cr.omega.random.max"),
    IV_CR_OMEGA("cr.omega"),
    IV_CR_SIMS_PER_PC("cr.n"),
    // NunnerBuskens
    IV_NB_EP_STRUCTURE("nb.ep.structure"),
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
    IV_NB_SIGMA_RANDOM("nb.sigma.random"),
    IV_NB_SIGMA_RANDOM_MIN("nb.sigma.random.min"),
    IV_NB_SIGMA_RANDOM_MAX("nb.sigma.random.max"),
    IV_NB_SIGMA("nb.sigma"),
    IV_NB_GAMMA_RANDOM("nb.gamma.random"),
    IV_NB_GAMMA_RANDOM_MIN("nb.gamma.random.min"),
    IV_NB_GAMMA_RANDOM_MAX("nb.gamma.random.max"),
    IV_NB_GAMMA("nb.gamma"),
    IV_NB_TAU_RANDOM("nb.tau.random"),
    IV_NB_TAU_RANDOM_MIN("nb.tau.random.min"),
    IV_NB_TAU_RANDOM_MAX("nb.tau.random.max"),
    IV_NB_TAU("nb.tau"),
    IV_NB_R_MIN_RANDOM("nb.r.min.random"),
    IV_NB_R_MIN_RANDOM_MIN("nb.r.min.random.min"),
    IV_NB_R_MIN_RANDOM_MAX("nb.r.min.random.max"),
    IV_NB_R_MIN("nb.r.min"),
    IV_NB_R_MAX_RANDOM("nb.r.max.random"),
    IV_NB_R_MAX_RANDOM_MIN("nb.r.max.random.min"),
    IV_NB_R_MAX_RANDOM_MAX("nb.r.max.random.max"),
    IV_NB_R_MAX("nb.r.max"),
    IV_NB_RS_EQUAL("nb.rs.equal"),
    IV_NB_R_SIGMA_RANDOM("nb.r.sigma.random"),
    IV_NB_R_SIGMA_RANDOM_HOMOGENEOUS("nb.r.sigma.random.homogeneous"),
    IV_NB_R_SIGMA("nb.r.sigma"),
    IV_NB_R_SIGMA_AV("nb.r.sigma.av"),
    IV_NB_R_SIGMA_NEIGHBORHOOD("nb.r.sigma.neighborhood"),
    IV_NB_R_PI_RANDOM("nb.r.pi.random"),
    IV_NB_R_PI_RANDOM_HOMOGENEOUS("nb.r.pi.random.homogeneous"),
    IV_NB_R_PI("nb.r.pi"),
    IV_NB_R_PI_AV("nb.r.pi.av"),
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
    IV_NB_PSI_RANDOM("nb.psi.random"),
    IV_NB_PSI_RANDOM_MIN("nb.psi.random.min"),
    IV_NB_PSI_RANDOM_MAX("nb.psi.random.max"),
    IV_NB_PSI("nb.psi"),
    IV_NB_XI_RANDOM("nb.xi.random"),
    IV_NB_XI_RANDOM_MIN("nb.xi.random.min"),
    IV_NB_XI_RANDOM_MAX("nb.xi.random.max"),
    IV_NB_XI("nb.xi"),
    IV_NB_OMEGA_RANDOM("nb.omega.random"),
    IV_NB_OMEGA_RANDOM_MIN("nb.omega.random.min"),
    IV_NB_OMEGA_RANDOM_MAX("nb.omega.random.max"),
    IV_NB_OMEGA("nb.omega"),
    IV_NB_ZETA("nb.zeta"),
    IV_NB_EPSILON("nb.epsilon"),
    IV_NB_SIMS_PER_PC("nb.n"),
    IV_NB_SIM_ITERATIONS("nb.m"),
    // NunnerBuskens genetic
    IV_NB_GEN_B1("nb.gen.b1"),
    IV_NB_GEN_B2("nb.gen.b2"),
    IV_NB_GEN_C1("nb.gen.c1"),
    IV_NB_GEN_PHI("nb.gen.phi"),
    IV_NB_GEN_PSI("nb.gen.psi"),
    IV_NB_GEN_XI("nb.gen.xi"),
    IV_NB_GEN_ROUNDS_MAX("nb.gen.rounds.max"),
    IV_NB_GEN_MUTATION_SD("nb.gen.mutation.sd"),
    IV_NB_GEN_FIRST_GENERATION("nb.gen.first.generation"),
    IV_NB_GEN_PARENTS("nb.gen.parents"),
    IV_NB_GEN_CHILDREN("nb.gen.children"),
    IV_NB_GEN_GENERATIONS("nb.gen.generations"),
    IV_NB_GEN_N("nb.gen.N"),
    IV_NB_GEN_GENERATION("nb.gen.generation"),
    IV_NB_GEN_SIMPLE_ID("nb.gen.id.simple"),
    IV_NB_GEN_MOTHER("nb.gen.mother"),
    IV_NB_GEN_FATHER("nb.gen.father"),
    IV_NB_GEN_TARGET_AVDEGREE("nb.gen.target.avdegree"),
    IV_NB_GEN_TARGET_CLUSTERING("nb.gen.target.clustering"),
    IV_NB_GEN_INITIAL_ALPHA_MIN("nb.gen.initial.alpha.min"),
    IV_NB_GEN_INITIAL_ALPHA_MAX("nb.gen.initial.alpha.max"),
    IV_NB_GEN_CONSIDER_AGE("nb.gen.consider.age"),
    IV_NB_GEN_CONSIDER_PROFESSION("nb.gen.consider.profession"),
    // NunnerBuskens professions
    IV_NB_PROF_N("nb.prof.N"),
    IV_NB_PROF_PHI("nb.prof.phi"),
    IV_NB_PROF_PSI("nb.prof.psi"),
    IV_NB_PROF_XI("nb.prof.xi"),
    IV_NB_PROF_ZETA("nb.zeta"),
    IV_NB_PROF_B1("nb.prof.b1"),
    IV_NB_PROF_B2("nb.prof.b2"),
    IV_NB_PROF_C1("nb.prof.c1"),
    IV_NB_PROF_C2("nb.prof.c2"),
    IV_NB_PROF_ALPHA("nb.prof.alpha"),
    IV_NB_PROF_GAMMA("nb.prof.gamma"),
    IV_NB_PROF_TAU("nb.prof.tau"),
    IV_NB_PROF_CONSIDER_AGE("nb.prof.consider.age"),
    IV_NB_PROF_CONSIDER_PROFESSION("nb.prof.consider.profession"),
    IV_NB_PROF_ASSORTATIVITY_INIT_CONDITION("nb.prof.assortativity.init.condition"),
    IV_NB_PROF_ASSORTATIVITY_CONDITIONS("nb.prof.assortativity.conditions"),
    IV_NB_PROF_OMEGA("nb.prof.omega"),
    IV_NB_PROF_SIMS_PER_PC("nb.prof.n"),
    IV_NB_PROF_SIM_ITERATIONS("nb.prof.m"),
    IV_NB_PROF_LOCKDOWN_CONDITIONS("nb.prof.lockdown.conditions"),
    IV_NB_PROF_DEGREE_DISTRIBUTION_CONDITIONS("nb.prof.degree.distribution.conditions"),
    IV_NB_PROF_ROUNDS_MAX("nb.prof.rounds.max"),
    DV_NB_PROF_N("nb.prof.n."),
    DV_NB_PROF_DEGREE("nb.prof.degree."),
    DV_NB_PROF_DEGREE_THEORETIC("nb.prof.degree.theoretic."),
    DV_NB_PROF_DEGREE_DIFF_TOTAL("nb.prof.degree.diff.total."),
    DV_NB_PROF_DEGREE_DIFF_PERCENT("nb.prof.degree.diff.percent."),
    DV_NB_PROF_DEGREE_SD("nb.prof.degree.sd."),
    DV_NB_PROF_DEGREE_SD_THEORETIC("nb.prof.degree.sd.theoretic."),
    DV_NB_PROF_DEGREE_SD_DIFF_TOTAL("nb.prof.degree.sd.diff.total."),
    DV_NB_PROF_DEGREE_SD_DIFF_PERCENT("nb.prof.degree.sd.diff.total."),
    IV_NB_PROF_VACCINATED("nb.prof.vaccinated"),
    IV_NB_PROF_VACCINATED_N("nb.prof.vaccinated.n"),
    IV_NB_PROF_VACCINATED_PERCENT("nb.prof.vaccinated.percent"),
    IV_NB_PROF_VACCINE_EFFICACY("nb.prof.vaccine.efficacy"),
    IV_NB_PROF_QUARANTINED("nb.prof.quarantined"),
    IV_NB_PROF_QUARANTINED_N("nb.prof.quarantined.n"),
    IV_NB_PROF_QUARANTINED_PERCENT("nb.prof.quarantined.percent"),

    // PROPERTIES (dependent variables)
    // simulation
    DV_SIM_STAGE("sim.stage"),
    DV_SIM_EPIDEMIC_DURATION("sim.epidemic.duration"),
    // network
    DV_NET_PERCENTAGE_SUSCEPTIBLE("net.pct.sus"),
    DV_NET_PERCENTAGE_INFECTED("net.pct.inf"),
    DV_NET_PERCENTAGE_RECOVERED("net.pct.rec"),
    DV_NET_PERCENTAGE_VACCINATED("net.pct.vac"),
    DV_NET_PERCENTAGE_SATISFIED("net.pct.satisfied"),
    DV_NET_PERCENTAGE_UNSATISFIED("net.pct.unsatisfied"),
    DV_NET_STABLE("net.stable"),
    DV_NET_STABLE_PRE("net.stable.pre"),
    DV_NET_STABLE_POST("net.stable.post"),
    DV_NET_DENSITY("net.density"),
    DV_NET_DENSITY_PRE("net.density.pre.epidemic"),
    DV_NET_DENSITY_POST("net.density.post.epidemic"),
    DV_NET_ASSORTATIVITY_CONDITIONS("net.assortativity.conditions"),
    DV_NET_ASSORTATIVITY_RISK_PERCEPTION("net.assortativity.risk.perception"),
    DV_NET_ASSORTATIVITY_RISK_PERCEPTION_PRE("net.assortativity.risk.perception.pre.epidemic"),
    DV_NET_ASSORTATIVITY_RISK_PERCEPTION_POST("net.assortativity.risk.perception.post.epidemic"),
    DV_NET_ASSORTATIVITY_AGE("net.assortativity.age"),
    DV_NET_ASSORTATIVITY_AGE_PRE("net.assortativity.age.pre.epidemic"),
    DV_NET_ASSORTATIVITY_AGE_POST("net.assortativity.age.post.epidemic"),
    DV_NET_ASSORTATIVITY_PROFESSION("net.assortativity.profession"),
    DV_NET_ASSORTATIVITY_PROFESSION_PRE("net.assortativity.profession.pre.epidemic"),
    DV_NET_ASSORTATIVITY_PROFESSION_POST("net.assortativity.profession.post.epidemic"),
    DV_NET_AV_DEGREE("net.degree.av"),
    DV_NET_AV_DEGREE_THEORETIC("net.degree.av.theoretic"),
    DV_NET_AV_DEGREE_DIFF_TOTAL("net.degree.av.diff.total"),
    DV_NET_AV_DEGREE_DIFF_PERCENT("net.degree.av.diff.percent"),
    DV_NET_AV_DEGREE_PRE("net.degree.pre.epidemic.av"),
    DV_NET_AV_DEGREE_POST("net.degree.post.epidemic.av"),
    DV_NET_AV_DEGREE2("net.degree2.av"),
    DV_NET_AV_DEGREE2_PRE("net.degree2.pre.epidemic.av"),
    DV_NET_AV_DEGREE2_POST("net.degree2.post.epidemic.av"),
    DV_NET_AV_DEGREE_SATISFIED("net.degree.av.satisfied"),
    DV_NET_AV_DEGREE_UNSATISFIED("net.degree.av.unsatisfied"),
    DV_NET_AV_BETWEENNESS("net.betweenness.av"),
    DV_NET_AV_BETWEENNESS_PRE("net.betweenness.pre.epidemic.av"),
    DV_NET_AV_BETWEENNESS_POST("net.betweenness.post.epidemic.av"),
    DV_NET_AV_CLOSENESS("net.closeness.av"),
    DV_NET_AV_CLOSENESS_PRE("net.closeness.pre.epidemic.av"),
    DV_NET_AV_CLOSENESS_POST("net.closeness.post.epidemic.av"),
    DV_NET_AV_CLUSTERING("net.clustering.av"),
    DV_NET_AV_CLUSTERING_PRE("net.clustering.pre.epidemic.av"),
    DV_NET_AV_CLUSTERING_POST("net.clustering.post.epidemic.av"),
    DV_NET_AV_PATHLENGTH("net.pathlength.av"),
    DV_NET_AV_PATHLENGTH_PRE("net.pathlength.pre.epidemic.av"),
    DV_NET_AV_PATHLENGTH_POST("net.pathlength.post.epidemic.av"),
    DV_NET_AV_UTIL_PRE("net.utility.pre.epidemic.av"),
    DV_NET_AV_UTIL_POST("net.utility.post.epidemic.av"),
    DV_NET_AV_BENEFIT_SOCIAL_PRE("net.benefit.social.pre.epidemic.av"),
    DV_NET_AV_BENEFIT_SOCIAL_POST("net.benefit.social.post.epidemic.av"),
    DV_NET_AV_COSTS_SOCIAL_PRE("net.costs.social.pre.epidemic.av"),
    DV_NET_AV_COSTS_SOCIAL_POST("net.costs.social.post.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_PRE("net.costs.disease.pre.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_POST("net.costs.disease.post.epidemic.av"),
    DV_NET_TIES_BROKEN_EPIDEMIC("net.ties.broken.epidemic"),
    DV_NET_NETWORK_CHANGES_EPIDEMIC("net.network.changes.epidemic"),
    DV_NET_INITIAL_INDEX_DISTANCE("net.initial.index.distance"),
    DV_NET_GEN_FITNESS_AV_DEGREE("net.gen.fitness.degree.av"),
    DV_NET_GEN_FITNESS_CLUSTERING("net.gen.fitness.clustering"),
    DV_NET_GEN_FITNESS_OVERALL("net.gen.fitness.overall"),
    DV_NET_EPIDEMIC_FINAL_SIZE("net.epidemic.final.size"),
    DV_NET_EPIDEMIC_DURATION("net.epidemic.duration"),
    DV_NET_EPIDEMIC_PEAK_TIME("net.epidemic.peak.time"),
    DV_NET_EPIDEMIC_PEAK_SIZE("net.epidemic.peak.size"),
    // agent
    DV_AGENT_SATISFIED("agent.satisfied"),
    DV_AGENT_UTIL("agent.util"),
    DV_AGENT_BENEFIT_SOCIAL("agent.benefit.social"),
    DV_AGENT_COSTS_SOCIAL("agent.costs.social"),
    DV_AGENT_COSTS_DISEASE("agent.costs.disease"),
    DV_AGENT_DISEASE_STATE("agent.dis.state"),
    DV_AGENT_DISEASE_ROUNDS_REMAINING("agent.dis.rounds.remaining"),
    DV_AGENT_INFECTED("agent.infected"),
    DV_AGENT_FORCE_INFECTED("agent.force.infected"),
    DV_AGENT_DEGREE1("agent.degree"),
    DV_AGENT_DEGREE2("agent.degree2"),
    DV_AGENT_CLOSENESS("agent.closeness"),
    DV_AGENT_CLUSTERING("agent.clustering"),
    DV_AGENT_BETWEENNESS("agent.betweenness"),
    DV_AGENT_BETWEENNESS_NORMALIZED("agent.betweenness.normalized"),
    DV_AGENT_ASSORTATIVITY_RISK_PERCEPTION("agent.assortativity.risk.perception"),
    DV_AGENT_ASSORTATIVITY_AGE("agent.assortativity.age"),
    DV_AGENT_ASSORTATIVITY_PROFESSION("agent.assortativity.profession"),
    DV_AGENT_CONS_BROKEN_ACTIVE("agent.cons.broken.active"),
    DV_AGENT_CONS_BROKEN_PASSIVE("agent.cons.broken.passive"),
    DV_AGENT_CONS_OUT_ACCEPTED("agent.cons.out.accepted"),
    DV_AGENT_CONS_OUT_DECLINED("agent.cons.out.declined"),
    DV_AGENT_CONS_IN_ACCEPTED("agent.cons.in.accepted"),
    DV_AGENT_CONS_IN_DECLINED("agent.cons.in.declined"),
    DV_AGENT_CONS_BROKEN_ACTIVE_EPIDEMIC("agent.cons.broken.active.epidemic"),
    DV_AGENT_CONS_BROKEN_PASSIVE_EPIDEMIC("agent.cons.broken.passive.epidemic"),
    DV_AGENT_CONS_OUT_ACCEPTED_EPIDEMIC("agent.cons.out.accepted.epidemic"),
    DV_AGENT_CONS_OUT_DECLINED_EPIDEMIC("agent.cons.out.declined.epidemic"),
    DV_AGENT_CONS_IN_ACCEPTED_EPIDEMIC("agent.cons.in.accepted.epidemic"),
    DV_AGENT_CONS_IN_DECLINED_EPIDEMIC("agent.cons.in.declined.epidemic"),
    DV_AGENT_PROFESSION("agent.profession"),
    // neighborhood
    DV_AGENT_NEIGHBORHOOD_R_SIGMA_AV("agent.neighborhood.r.sigma.av"),
    DV_AGENT_NEIGHBORHOOD_R_PI_AV("agent.neighborhood.r.pi.av"),
    DV_AGENT_INDEX_NEIGHBORHOOD_R_SIGMA_AV("agent.index.neighborhood.r.sigma.av"),
    DV_AGENT_INDEX_NEIGHBORHOOD_R_PI_AV("agent.index.neighborhood.r.pi.av"),
    DV_AGENT_INDEX_DISTANCE("agent.index.distance"),
    // index
    DV_INDEX_SATISFIED("index.satisfied"),
    DV_INDEX_UTIL("index.util"),
    DV_INDEX_BENEFIT_SOCIAL("index.benefit.social"),
    DV_INDEX_COSTS_SOCIAL("index.costs.social"),
    DV_INDEX_COSTS_DISEASE("index.costs.disease"),
    DV_INDEX_DISEASE_STATE("index.dis.state"),
    DV_INDEX_DISEASE_ROUNDS_REMAINING("index.dis.rounds.remaining"),
    DV_INDEX_DEGREE1("index.degree"),
    DV_INDEX_DEGREE2("index.degree2"),
    DV_INDEX_CLOSENESS("index.closeness"),
    DV_INDEX_CLUSTERING("index.clustering"),
    DV_INDEX_BETWEENNESS("index.betweenness"),
    DV_INDEX_BETWEENNESS_NORMALIZED("index.betweenness.normalized"),
    DV_INDEX_ASSORTATIVITY_RISK_PERCEPTION("index.assortativity.perception"),
    DV_INDEX_ASSORTATIVITY_AGE("index.assortativity.age"),
    DV_INDEX_ASSORTATIVITY_PROFESSION("index.assortativity.profession"),
    DV_INDEX_CONS_BROKEN_ACTIVE("index.cons.broken.active"),
    DV_INDEX_CONS_BROKEN_PASSIVE("index.cons.broken.passive"),
    DV_INDEX_CONS_OUT_ACCEPTED("index.cons.out.accepted"),
    DV_INDEX_CONS_OUT_DECLINED("index.cons.out.declined"),
    DV_INDEX_CONS_IN_ACCEPTED("index.cons.in.accepted"),
    DV_INDEX_CONS_IN_DECLINED("index.cons.in.declined"),
    DV_INDEX_R_SIGMA("index.r.sigma"),
    DV_INDEX_R_SIGMA_NEIGHBORHOOD("index.r.sigma.neighborhood"),
    DV_INDEX_R_PI("index.r.pi"),
    DV_INDEX_R_PI_NEIGHBORHOOD("index.r.pi.neighborhood"),
    DV_INDEX_PROFESSION("index.profession"),

    // static
    DV_NET_STATIC_EPIDEMIC_DURATION("net.static.epidemic.duration"),
    DV_NET_STATIC_EPIDEMIC_PEAK("net.static.epidemic.peak"),
    DV_NET_STATIC_EPIDEMIC_PEAK_SIZE("net.static.epidemic.peak.size"),
    DV_NET_STATIC_PERCENTAGE_SUSCEPTIBLE("net.static.pct.sus"),
    DV_NET_STATIC_PERCENTAGE_INFECTED("net.static.pct.inf"),
    DV_NET_STATIC_PERCENTAGE_RECOVERED("net.static.pct.rec"),
    DV_NET_STATIC_TIES_BROKEN_EPIDEMIC("net.static.ties.broken.epidemic"),
    DV_NET_STATIC_NETWORK_CHANGES_EPIDEMIC("net.static.network.changes.epidemic"),
    DV_NET_STATIC_CONS_BROKEN_ACTIVE_EPIDEMIC("net.static.ties.broken.active.epidemic"),
    DV_NET_STATIC_CONS_BROKEN_PASSIVE_EPIDEMIC("net.static.ties.broken.passive.epidemic"),
    DV_NET_STATIC_CONS_OUT_ACCEPTED_EPIDEMIC("net.static.ties.out.accepted.epidemic"),
    DV_NET_STATIC_CONS_OUT_DECLINED_EPIDEMIC("net.static.ties.out.declined.epidemic"),
    DV_NET_STATIC_CONS_IN_ACCEPTED_EPIDEMIC("net.static.ties.in.accepted.epidemic"),
    DV_NET_STATIC_CONS_IN_DECLINED_EPIDEMIC("net.static.ties.in.declined.epidemic"),
    DV_NET_STATIC_AV_DEGREE_POST("net.static.av.degree.post"),
    DV_NET_STATIC_AV_DEGREE2_POST("net.static.av.degree2.post"),
    DV_NET_STATIC_AV_CLOSENESS_POST("net.static.av.closesness.post"),
    DV_NET_STATIC_AV_CLUSTERING_POST("net.static.av.clustering.post"),
    DV_NET_STATIC_AV_PATHLENGTH_POST("net.static.av.pathlength.post"),
    DV_NET_STATIC_AV_UTIL_POST("net.static.av.util.post"),
    DV_NET_STATIC_AV_BENEFIT_SOCIAL_POST("net.static.av.benefit.social.post"),
    DV_NET_STATIC_AV_COSTS_SOCIAL_POST("net.static.av.costs.social.post"),
    DV_NET_STATIC_AV_COSTS_DISEASE_POST("net.static.av.costs.disease.post"),
    DV_NET_STATIC_DENSITY_POST("net.static.density.post"),
    DV_NET_STATIC_ASSORTATIVITY_CONDITIONs("net.static.assortativity.conditions"),
    DV_NET_STATIC_ASSORTATIVITY_RISK_PERCEPTION_POST("net.static.assortativity.risk.perception.post"),
    DV_NET_STATIC_ASSORTATIVITY_AGE_POST("net.static.assortativity.age.post"),
    DV_NET_STATIC_ASSORTATIVITY_PROFESSION_POST("net.static.assortativity.profession.post"),
    DV_NET_STATIC_STABLE_POST("net.static.stable.post"),
    DV_INDEX_STATIC_SATISFIED("index.static.satisfied"),
    DV_INDEX_STATIC_UTIL("index.static.util"),
    DV_INDEX_STATIC_BENEFIT_SOCIAL("index.static.benefit.social"),
    DV_INDEX_STATIC_COSTS_SOCIAL("index.static.costs.social"),
    DV_INDEX_STATIC_COSTS_DISEASE("index.static.costs.disease"),
    DV_INDEX_STATIC_DISEASE_STATE("index.static.disease.state"),
    DV_INDEX_STATIC_DISEASE_ROUNDS_REMAINING("index.static.disease.rounds.remaining"),
    DV_INDEX_STATIC_DEGREE1("index.static.degree1"),
    DV_INDEX_STATIC_DEGREE2("index.static.degree2"),
    DV_INDEX_STATIC_CLOSENESS("index.static.closeness"),
    DV_INDEX_STATIC_CLUSTERING("index.static.clustering"),
    DV_INDEX_STATIC_BETWEENNESS("index.static.betweenness"),
    DV_INDEX_STATIC_BETWEENNESS_NORMALIZED("index.static.betweenness.normalized"),
    DV_INDEX_STATIC_CONS_BROKEN_ACTIVE("index.static.cons.broken.active"),
    DV_INDEX_STATIC_CONS_BROKEN_PASSIVE("index.static.cons.broken.passive"),
    DV_INDEX_STATIC_CONS_OUT_ACCEPTED("index.static.cons.out.accepted"),
    DV_INDEX_STATIC_CONS_OUT_DECLINED("index.static.cons.out.declined"),
    DV_INDEX_STATIC_CONS_IN_ACCEPTED("index.static.cons.in.accepted"),
    DV_INDEX_STATIC_CONS_IN_DECLINED("index.static.cons.in.declined"),
    DV_INDEX_STATIC_R_SIGMA("index.static.r.sigma"),
    DV_INDEX_STATIC_R_PI("index.static.r.pi"),
    // dynamic
    DV_NET_DYNAMIC_EPIDEMIC_DURATION("net.dynamic.epidemic.duration"),
    DV_NET_DYNAMIC_EPIDEMIC_PEAK("net.dynamic.epidemic.peak"),
    DV_NET_DYNAMIC_EPIDEMIC_PEAK_SIZE("net.dynamic.epidemic.peak.size"),
    DV_NET_DYNAMIC_PERCENTAGE_SUSCEPTIBLE("net.dynamic.pct.sus"),
    DV_NET_DYNAMIC_PERCENTAGE_INFECTED("net.dynamic.pct.inf"),
    DV_NET_DYNAMIC_PERCENTAGE_RECOVERED("net.dynamic.pct.rec"),
    DV_NET_DYNAMIC_TIES_BROKEN_EPIDEMIC("net.dynamic.ties.broken.epidemic"),
    DV_NET_DYNAMIC_NETWORK_CHANGES_EPIDEMIC("net.dynamic.network.changes.epidemic"),
    DV_NET_DYNAMIC_CONS_BROKEN_ACTIVE_EPIDEMIC("net.dynamic.ties.broken.active.epidemic"),
    DV_NET_DYNAMIC_CONS_BROKEN_PASSIVE_EPIDEMIC("net.dynamic.ties.broken.passive.epidemic"),
    DV_NET_DYNAMIC_CONS_OUT_ACCEPTED_EPIDEMIC("net.dynamic.ties.out.accepted.epidemic"),
    DV_NET_DYNAMIC_CONS_OUT_DECLINED_EPIDEMIC("net.dynamic.ties.out.declined.epidemic"),
    DV_NET_DYNAMIC_CONS_IN_ACCEPTED_EPIDEMIC("net.dynamic.ties.in.accepted.epidemic"),
    DV_NET_DYNAMIC_CONS_IN_DECLINED_EPIDEMIC("net.dynamic.ties.in.declined.epidemic"),
    DV_NET_DYNAMIC_AV_DEGREE_POST("net.dynamic.av.degree.post"),
    DV_NET_DYNAMIC_AV_DEGREE2_POST("net.dynamic.av.degree2.post"),
    DV_NET_DYNAMIC_AV_CLOSENESS_POST("net.dynamic.av.closesness.post"),
    DV_NET_DYNAMIC_AV_CLUSTERING_POST("net.dynamic.av.clustering.post"),
    DV_NET_DYNAMIC_AV_PATHLENGTH_POST("net.dynamic.av.pathlength.post"),
    DV_NET_DYNAMIC_AV_UTIL_POST("net.dynamic.av.util.post"),
    DV_NET_DYNAMIC_AV_BENEFIT_SOCIAL_POST("net.dynamic.av.benefit.social.post"),
    DV_NET_DYNAMIC_AV_COSTS_SOCIAL_POST("net.dynamic.av.costs.social.post"),
    DV_NET_DYNAMIC_AV_COSTS_DISEASE_POST("net.dynamic.av.costs.disease.post"),
    DV_NET_DYNAMIC_DENSITY_POST("net.dynamic.density.post"),
    DV_NET_DYNAMIC_ASSORTATIVITY_CONDITIONs("net.dynamic.assortativity.conditions"),
    DV_NET_DYNAMIC_ASSORTATIVITY_RISK_PERCEPTION_POST("net.dynamic.assortativity.risk.perception.post"),
    DV_NET_DYNAMIC_ASSORTATIVITY_AGE_POST("net.dynamic.assortativity.age.post"),
    DV_NET_DYNAMIC_ASSORTATIVITY_PROFESSION_POST("net.dynamic.assortativity.profession.post"),
    DV_NET_DYNAMIC_STABLE_POST("net.dynamic.stable.post"),
    DV_INDEX_DYNAMIC_SATISFIED("index.dynamic.satisfied"),
    DV_INDEX_DYNAMIC_UTIL("index.dynamic.util"),
    DV_INDEX_DYNAMIC_BENEFIT_SOCIAL("index.dynamic.benefit.social"),
    DV_INDEX_DYNAMIC_COSTS_SOCIAL("index.dynamic.costs.social"),
    DV_INDEX_DYNAMIC_COSTS_DISEASE("index.dynamic.costs.disease"),
    DV_INDEX_DYNAMIC_DISEASE_STATE("index.dynamic.disease.state"),
    DV_INDEX_DYNAMIC_DISEASE_ROUNDS_REMAINING("index.dynamic.disease.rounds.remaining"),
    DV_INDEX_DYNAMIC_DEGREE1("index.dynamic.degree1"),
    DV_INDEX_DYNAMIC_DEGREE2("index.dynamic.degree2"),
    DV_INDEX_DYNAMIC_CLOSENESS("index.dynamic.closeness"),
    DV_INDEX_DYNAMIC_CLUSTERING("index.dynamic.clustering"),
    DV_INDEX_DYNAMIC_BETWEENNESS("index.dynamic.betweenness"),
    DV_INDEX_DYNAMIC_BETWEENNESS_NORMALIZED("index.dynamic.betweenness.normalized"),
    DV_INDEX_DYNAMIC_CONS_BROKEN_ACTIVE("index.dynamic.cons.broken.active"),
    DV_INDEX_DYNAMIC_CONS_BROKEN_PASSIVE("index.dynamic.cons.broken.passive"),
    DV_INDEX_DYNAMIC_CONS_OUT_ACCEPTED("index.dynamic.cons.out.accepted"),
    DV_INDEX_DYNAMIC_CONS_OUT_DECLINED("index.dynamic.cons.out.declined"),
    DV_INDEX_DYNAMIC_CONS_IN_ACCEPTED("index.dynamic.cons.in.accepted"),
    DV_INDEX_DYNAMIC_CONS_IN_DECLINED("index.dynamic.cons.in.declined"),
    DV_INDEX_DYNAMIC_R_SIGMA("index.dynamic.r.sigma"),
    DV_INDEX_DYNAMIC_R_PI("index.dynamic.r.pi"),


    // OTHERS
    EXPORT_FILENAME("export.filename");


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

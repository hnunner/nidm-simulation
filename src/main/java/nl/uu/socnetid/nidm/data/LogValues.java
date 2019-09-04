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
    IV_CIDM_NET_SIZE("cidm.N"),
    IV_CIDM_IOTA("cidm.iota"),
    IV_CIDM_PHI("cidm.phi"),
    IV_CIDM_PHI_AV("cidm.phi.av"),
    IV_CIDM_TAU("cidm.tau"),
    IV_CIDM_TAU_AV("cidm.tau.av"),
    IV_CIDM_ZETA("cidm.zeta"),
    IV_CIDM_EPSILON("cidm.epsilon"),
    IV_CIDM_SIMS_PER_PC("cidm.n"),

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
    DV_NET_AV_UTIL_PRE("net.utility.pre.epidemic.av"),
    DV_NET_AV_UTIL_POST("net.utility.post.epidemic.av"),
    DV_NET_AV_BENEFIT_DIST1_PRE("net.benefit.distance1.pre.epidemic.av"),
    DV_NET_AV_BENEFIT_DIST1_POST("net.benefit.distance1.post.epidemic.av"),
    DV_NET_AV_BENEFIT_DIST2_PRE("net.benefit.distance2.pre.epidemic.av"),
    DV_NET_AV_BENEFIT_DIST2_POST("net.benefit.distance2.post.epidemic.av"),
    DV_NET_AV_COSTS_DIST1_PRE("net.costs.distance1.pre.epidemic.av"),
    DV_NET_AV_COSTS_DIST1_POST("net.costs.distance1.post.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_PRE("net.costs.disease.pre.epidemic.av"),
    DV_NET_AV_COSTS_DISEASE_POST("net.costs.disease.post.epidemic.av"),
    DV_NET_TIES_BROKEN_EPIDEMIC("net.ties.broken.epidemic"),
    // agent
    DV_AGENT_SATISFIED("agent.satisfied"),
    DV_AGENT_UTIL("agent.util"),
    DV_AGENT_BENEFIT_DIST1("agent.benefit.distance.1"),
    DV_AGENT_BENEFIT_DIST2("agent.benefit.distance.2"),
    DV_AGENT_COSTS_DIST1("agent.costs.distance.1"),
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

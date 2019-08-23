package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public enum LogProperties {

    // simulation
    SIM_ROUND("prop.sim.round"),
    SIM_STAGE("prop.sim.stage"),
    SIM_EPIDEMIC_DURATION("prop.sim.epidemic.duration"),
    // network
    NET_PERCENTAGE_SUSCEPTIBLE("prop.net.pct.sus"),
    NET_PERCENTAGE_INFECTED("prop.net.pct.inf"),
    NET_PERCENTAGE_RECOVERED("prop.net.pct.rec"),
    NET_STABLE("prop.net.stable"),
    NET_DENSITY("prop.net.density"),
    NET_DENSITY_PRE("prop.net.density.pre.epidemic"),
    NET_DENSITY_POST("prop.net.density.post.epidemic"),
    NET_AV_DEGREE("prop.net.av.degree"),
    NET_AV_DEGREE_PRE("prop.net.av.degree.pre.epidemic"),
    NET_AV_DEGREE_POST("prop.net.av.degree.post.epidemic"),
    NET_AV_DEGREE2_PRE("prop.net.av.degree2.pre.epidemic"),
    NET_AV_DEGREE2_POST("prop.net.av.degree2.post.epidemic"),
    NET_AV_CLOSENESS_PRE("prop.net.av.closeness.pre.epidemic"),
    NET_AV_CLOSENESS_POST("prop.net.av.closeness.post.epidemic"),
    NET_AV_CLUSTERING("prop.net.av.clustering"),
    NET_AV_CLUSTERING_PRE("prop.net.av.clustering.pre.epidemic"),
    NET_AV_CLUSTERING_POST("prop.net.av.clustering.post.epidemic"),
    NET_AV_UTIL_PRE("prop.net.av.utility.pre.epidemic"),
    NET_AV_UTIL_POST("prop.net.av.utility.post.epidemic"),
    NET_AV_BENEFIT_DIST1_PRE("prop.net.av.benefit.distance1.pre.epidemic"),
    NET_AV_BENEFIT_DIST1_POST("prop.net.av.benefit.distance1.post.epidemic"),
    NET_AV_BENEFIT_DIST2_PRE("prop.net.av.benefit.distance2.pre.epidemic"),
    NET_AV_BENEFIT_DIST2_POST("prop.net.av.benefit.distance2.post.epidemic"),
    NET_AV_COSTS_DIST1_PRE("prop.net.av.costs.distance1.pre.epidemic"),
    NET_AV_COSTS_DIST1_POST("prop.net.av.costs.distance1.post.epidemic"),
    NET_AV_COSTS_DISEASE_PRE("prop.net.av.costs.disease.pre.epidemic"),
    NET_AV_COSTS_DISEASE_POST("prop.net.av.costs.disease.post.epidemic"),
    NET_TIES_BROKEN_EPIDEMIC("prop.net.ties.broken.epidemic"),
    // agent
    AGENT_SATISFIED("prop.agent.satisfied"),
    AGENT_UTIL("prop.agent.util"),
    AGENT_BENEFIT_DIST1("prop.agent.benefit.distance.1"),
    AGENT_BENEFIT_DIST2("prop.agent.benefit.distance.2"),
    AGENT_COSTS_DIST1("prop.agent.costs.distance.1"),
    AGENT_COSTS_DISEASE("prop.agent.costs.disease"),
    AGENT_DISEASE_STATE("prop.agent.dis.state"),
    AGENT_DISEASE_ROUNDS_REMAINING("prop.agent.dis.rounds.remaining"),
    AGENT_DEGREE1("prop.agent.net.degree"),
    AGENT_DEGREE2("prop.agent.net.degree2"),
    AGENT_CLOSENESS("prop.agent.net.closeness"),
    AGENT_CLUSTERING("prop.agent.net.clustering"),
    AGENT_CONS_BROKEN_ACTIVE("prop.agent.cons.broken.active"),
    AGENT_CONS_BROKEN_PASSIVE("prop.agent.cons.broken.passive"),
    AGENT_CONS_OUT_ACCEPTED("prop.agent.cons.out.accepted"),
    AGENT_CONS_OUT_DECLINED("prop.agent.cons.out.declined"),
    AGENT_CONS_IN_ACCEPTED("prop.agent.cons.in.accepted"),
    AGENT_CONS_IN_DECLINED("prop.agent.cons.in.declined");


    // properties feasible for regression analysis
    private static final LogProperties[] REG_PARAMS = {
            NET_DENSITY_PRE,
            NET_AV_DEGREE_PRE,
            // NET_AV_DEGREE2_PRE,
            NET_AV_CLOSENESS_PRE,
            NET_AV_CLUSTERING_PRE,
            AGENT_DEGREE1,
            // AGENT_DEGREE2,
            AGENT_CLOSENESS,
            AGENT_CLUSTERING};

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    LogProperties(String name) {
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
     * Gets the properties feasible for regression analyses.
     *
     * @return the properties feasible for regression analyses
     */
    public static LogProperties[] getRegressionProperties() {
        return REG_PARAMS;
    }

}

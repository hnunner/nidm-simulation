package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public enum AgentProperties {

    SATISFIED("act.prop.satisfied"),
    UTIL("act.prop.util"),
    BENEFIT_DIST1("act.prop.benefit.distance.1"),
    BENEFIT_DIST2("act.prop.benefit.distance.2"),
    COSTS_DIST1("act.prop.costs.distance.1"),
    COSTS_DISEASE("act.prop.costs.disease"),
    DISEASE_STATE("act.prop.dis.state"),
    DISEASE_ROUNDS_REMAINING("act.prop.dis.rounds.remaining"),
    DEGREE1("act.prop.net.degree.order.1"),
    DEGREE2("act.prop.net.degree.order.2"),
    CLOSENESS("act.prop.net.closeness"),
    CLUSTERING("act.prop.net.clustering"),
    CONS_BROKEN_ACTIVE("act.prop.cons.broken.active"),
    CONS_BROKEN_PASSIVE("act.prop.cons.broken.passive"),
    CONS_OUT_ACCEPTED("act.prop.cons.out.accepted"),
    CONS_OUT_DECLINED("act.prop.cons.out.declined"),
    CONS_IN_ACCEPTED("act.prop.cons.in.accepted"),
    CONS_IN_DECLINED("act.prop.cons.in.declined");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    AgentProperties(String name) {
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

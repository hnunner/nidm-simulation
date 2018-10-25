package nl.uu.socnetid.netgame.io.types;

/**
 * @author Hendrik Nunner
 */
public enum ActorProperties {

//    agentsDetailsCSVCols.add("act.prop.satisfied");
//    agentsDetailsCSVCols.add("act.prop.util.overall");
//    agentsDetailsCSVCols.add("act.prop.util.benefit.distance.1");
//    agentsDetailsCSVCols.add("act.prop.util.benefit.distance.2");
//    agentsDetailsCSVCols.add("act.prop.util.costs.distance.1");
//    agentsDetailsCSVCols.add("act.prop.util.costs.disease");
//    agentsDetailsCSVCols.add("act.prop.dis.state");
//    agentsDetailsCSVCols.add("act.prop.dis.rounds.until.recovered");
//  agentsDetailsCSVCols.add("act.stats.net.degree.order.1");
//  agentsDetailsCSVCols.add("act.stats.net.degree.order.2");
//  agentsDetailsCSVCols.add("act.stats.net.closeness");
//  agentsDetailsCSVCols.add("act.stats.cons.broken.active");
//  agentsDetailsCSVCols.add("act.stats.cons.broken.passive");
//  agentsDetailsCSVCols.add("act.stats.cons.out.accepted");
//  agentsDetailsCSVCols.add("act.stats.cons.out.declined");
//  agentsDetailsCSVCols.add("act.stats.cons.in.accepted");
//  agentsDetailsCSVCols.add("act.stats.cons.in.declined");

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
    ActorProperties(String name) {
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

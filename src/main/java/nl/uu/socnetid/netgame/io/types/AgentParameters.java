package nl.uu.socnetid.netgame.io.types;

/**
 * @author Hendrik Nunner
 */
public enum AgentParameters {

//    agentsDetailsCSVCols.add("act.id");
//    agentsDetailsCSVCols.add("act.param.risk.factor");
//    agentsDetailsCSVCols.add("act.param.net.alpha");
//    agentsDetailsCSVCols.add("act.param.net.beta");
//    agentsDetailsCSVCols.add("act.param.net.c");
//    agentsDetailsCSVCols.add("act.param.dis.tau");
//    agentsDetailsCSVCols.add("act.param.dis.s");
//    agentsDetailsCSVCols.add("act.param.dis.gamma");
//    agentsDetailsCSVCols.add("act.param.dis.mu");

    ID("act.param.id"),
    R("act.param.r"),
    ALPHA("act.param.alpha"),
    BETA("act.param.beta"),
    C("act.param.c"),
    TAU("act.param.tau"),
    S("act.param.s"),
    GAMMA("act.param.gamma"),
    MU("act.param.mu");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    AgentParameters(String name) {
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

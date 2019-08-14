package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public enum AgentParameters {

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

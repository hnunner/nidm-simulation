package nl.uu.socnetid.netgame.simulation;

/**
 * @author Hendrik Nunner
 */
public enum SimulationStage {
    PRE_EPIDEMIC("pre-epidemic"),
    ACTIVE_EPIDEMIC("active-epidemic"),
    POST_EPIDEMIC("post-epidemic");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    SimulationStage(String name) {
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

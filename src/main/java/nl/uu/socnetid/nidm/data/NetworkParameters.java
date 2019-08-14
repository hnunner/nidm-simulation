package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public enum NetworkParameters {

    N("net.param.N"),
    ALPHA("net.param.alpha"),
    BETA("net.param.beta"),
    C("net.param.cost"),
    MU("net.param.mu"),
    R("net.param.r");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    NetworkParameters(String name) {
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

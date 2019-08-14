package nl.uu.socnetid.nidm.networks;

/**
 *
 * @author Hendrik Nunner
 */
public enum NetworkTypes {

    // TODO add missing types, such as bipartite
    EMPTY("empty"),
    FULL("full"),
    RING("ring"),
    STAR("star"),
    UNDEFINED("undefined");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    NetworkTypes(String name) {
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

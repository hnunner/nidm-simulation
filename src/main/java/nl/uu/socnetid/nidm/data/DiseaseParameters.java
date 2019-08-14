package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseParameters {

    S("dis.param.s"),
    TAU("dis.param.tau"),
    GAMMA("dis.param.gamma"),
    MU("dis.param.mu");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    DiseaseParameters(String name) {
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

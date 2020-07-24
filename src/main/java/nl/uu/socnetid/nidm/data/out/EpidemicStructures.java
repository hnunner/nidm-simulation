package nl.uu.socnetid.nidm.data.out;

/**
 *
 * @author Hendrik Nunner
 */
public enum EpidemicStructures {

    DYNAMIC("dynamic"),
    STATIC("static"),
    BOTH("both");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    EpidemicStructures(String name) {
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

    // TODO warning if not found!!!
    public static EpidemicStructures fromString(String name) {
        if (name.equals(DYNAMIC.toString())) {
            return DYNAMIC;
        } else if (name.equals(STATIC.toString())) {
            return STATIC;
        } else if (name.equals(BOTH.toString())) {
            return BOTH;
        }
        return null;
    }

}

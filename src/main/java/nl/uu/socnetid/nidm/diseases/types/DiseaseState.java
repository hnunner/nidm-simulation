package nl.uu.socnetid.nidm.diseases.types;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseState {
    INFECTIOUS("infectious"),
    DEFEATED("defeated");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    DiseaseState(String name) {
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
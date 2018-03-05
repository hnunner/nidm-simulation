package nl.uu.socnetid.networkgames.diseases.types;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseType {
    SIR("generic SIR");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    DiseaseType(String name) {
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

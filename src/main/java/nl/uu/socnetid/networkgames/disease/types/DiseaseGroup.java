package nl.uu.socnetid.networkgames.disease.types;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseGroup {
    SUSCEPTIBLE("susceptible"),
    INFECTED("infected"),
    RECOVERED("recovered");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    DiseaseGroup(String name) {
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

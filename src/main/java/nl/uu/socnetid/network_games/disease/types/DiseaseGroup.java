package nl.uu.socnetid.network_games.disease.types;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseGroup {
    SUSCEPTIBLE("SUS"),
    INFECTED("INF"),
    RECOVERED("REC");

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

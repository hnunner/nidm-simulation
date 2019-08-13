package nl.uu.socnetid.nidm.io.types;

/**
 * @author Hendrik Nunner
 */
public enum DiseaseProperties {

//    roundSummaryCSVCols.add("dis.stats.pct.sus");
//    roundSummaryCSVCols.add("dis.stats.pct.inf");
//    roundSummaryCSVCols.add("dis.stats.pct.rec");
//    roundSummaryCSVCols.add("dis.stats.duration");

    PERCENTAGE_SUSCEPTIBLE("dis.prop.pct.sus"),
    PERCENTAGE_INFECTED("dis.prop.pct.inf"),
    PERCENTAGE_RECOVERED("dis.prop.pct.rec"),
    DURATION("dis.prop.duration");


    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    DiseaseProperties(String name) {
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

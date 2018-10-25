package nl.uu.socnetid.netgame.io.types;

/**
 * @author Hendrik Nunner
 */
public enum NetworkParameters {

//  agentsDetailsCSVCols.add("net.param.size");
//  roundSummaryCSVCols.add("net.param.av.alpha");
//  roundSummaryCSVCols.add("net.param.av.beta");
//  roundSummaryCSVCols.add("net.param.av.cost");
//  roundSummaryCSVCols.add("net.param.av.mu");
//  roundSummaryCSVCols.add("net.param.av.risk.factor");
//  roundSummaryCSVCols.add("net.param.struct.start");

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

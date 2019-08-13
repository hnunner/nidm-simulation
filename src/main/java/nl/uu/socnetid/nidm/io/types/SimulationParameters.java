package nl.uu.socnetid.nidm.io.types;

/**
 * @author Hendrik Nunner
 */
public enum SimulationParameters {

//    agentsDetailsCSVCols.add("uid");
//    agentsDetailsCSVCols.add("upc.id");
//    agentsDetailsCSVCols.add("sim.id");
//    agentsDetailsCSVCols.add("sim.round");
//    agentsDetailsCSVCols.add("sim.stage");
//    ("filename");

    UID("sim.param.uid"),
    UPC("sim.param.upc"),
    SIM("sim.param.sim"),
    NETWORK_STRUCTURE("net.param.net.empty"),
    GEXF_EXPORT_FILE("sim.param.gexf.export.file");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    SimulationParameters(String name) {
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

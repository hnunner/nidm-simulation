package nl.uu.socnetid.netgame.io.types;

/**
 * @author Hendrik Nunner
 */
public enum SimulationProperties {

//    agentsDetailsCSVCols.add("uid");
//    agentsDetailsCSVCols.add("upc.id");
//    agentsDetailsCSVCols.add("sim.id");
//    agentsDetailsCSVCols.add("sim.round");
//    agentsDetailsCSVCols.add("sim.stage");
//    ("filename");


    SIM_ROUND("sim.prop.round"),
    SIM_STAGE("sim.prop.stage");

    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    SimulationProperties(String name) {
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

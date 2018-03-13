package nl.uu.socnetid.networkgames.actors;

/**
 * @author Hendrik Nunner
 */
public enum ActorAttributes {

    UTILITY_FUNCTION("utility.function"),
    DISEASE_SPECS("disease.specs"),
    DISEASE_GROUP("disease.group"),
    DISEASE_INFECTION("disease.infection"),
    UI_CLASS("ui.class"),
    RISK_FACTOR("risk.factor"),
    RISK_MEANING("risk.meaning"),
    SATISFIED("satisfied"),
    CONNECTION_STATS("connection.stats");

    // the name
    private String name;

    /**
     * Constructor, setting the name
     *
     * @param name
     *          the name of the enum
     */
    ActorAttributes(String name) {
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

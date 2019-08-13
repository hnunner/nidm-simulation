package nl.uu.socnetid.nidm.agents;

/**
 * @author Hendrik Nunner
 */
public enum AgentAttributes {

    UTILITY_FUNCTION("utility.function"),
    DISEASE_SPECS("disease.specs"),
    DISEASE_GROUP("disease.group"),
    DISEASE_INFECTION("disease.infection"),
    UI_CLASS("ui.class"),
    RISK_FACTOR_SIGMA("risk.factor.sigma"),
    RISK_FACTOR_PI("risk.factor.pi"),
    RISK_MEANING_SIGMA("risk.meaning.sigma"),
    RISK_MEANING_PI("risk.meaning.pi"),
    PHI("phi"),
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
    AgentAttributes(String name) {
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

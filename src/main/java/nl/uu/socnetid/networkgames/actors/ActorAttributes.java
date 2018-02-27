package nl.uu.socnetid.networkgames.actors;

/**
 * @author Hendrik Nunner
 */
public enum ActorAttributes {

    UI_CLASS("ui.class"),
    DISEASE_GROUP("disease.group"),
    DISEASE_TYPE("disease.type"),
    DISEASE_TIMEUNTILCURED("disease.timeuntilcured"),
    SATISFIED("satisfied");

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

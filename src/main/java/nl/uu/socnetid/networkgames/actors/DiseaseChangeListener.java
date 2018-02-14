package nl.uu.socnetid.networkgames.actors;

/**
 * @author Hendrik Nunner
 */
public interface DiseaseChangeListener {

    /**
     * Entry point for disease change notifications.
     *
     * @param actor
     *          the actor whose disease has changed
     */
    void notifyDiseaseChanged(final Actor actor);

}

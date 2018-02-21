package nl.uu.socnetid.networkgames.actors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Implementation of a simple {@link Actor}.
 *
 * @author Hendrik Nunner
 */
public class RationalActor extends AbstractActor implements Actor {

    // logger
	@SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(RationalActor.class);


    /**
     * Private constructor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     */
    protected RationalActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        super(utilityFunction, diseaseSpecs, RISK_NEUTRAL);
    }

    /**
     * Factory method returning a new {@link Actor} instance.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @return a new {@link Actor} instance
     */
    public static Actor newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return new RationalActor(utilityFunction, diseaseSpecs);
    }


    /**
     * Private constructor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the risk factor of the new actor
     */
    protected RationalActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        super(utilityFunction, diseaseSpecs, riskFactor);
    }

    /**
     * Factory method returning a new {@link Actor} instance with a custom risk factor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the custom risk factor
     * @return a new {@link Actor} instance with a custom risk factor
     */
    public static Actor newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        return new RationalActor(utilityFunction, diseaseSpecs, riskFactor);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.AbstractActor#checkToCreateConnection()
     */
    @Override
    public Actor seekNewConnection() {

        // should this consider non-infected first?
        // Q&A Feb 20th 2018: no, as even an infected might provide
        // high utility through many indirect connections
        Actor potentialConnection = getRandomNotYetConnectedActor();

        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.add(potentialConnection);

        // connect if new connection creates higher or equal utility
        if (this.getUtility(potentialConnections).getOverallUtility() >= this.getUtility().getOverallUtility()) {
            return potentialConnection;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.AbstractActor#checkToBreakConnection()
     */
    @Override
    public Actor seekCostlyConnection() {

        // should this consider infected first?
        // Q&A Feb 20th 2018: no, as even an infected might provide
        // high utility through many indirect connections
        Actor potentialRemoval = getRandomConnection();

        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.remove(potentialRemoval);

        // disconnect only if removal creates higher utility
        if (this.getUtility(potentialConnections).getOverallUtility() > this.getUtility().getOverallUtility()) {
            return potentialRemoval;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#requestConnection(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public boolean acceptConnection(Actor newConnection) {
        List<Actor> prospectiveConnections = new ArrayList<Actor>(this.getConnections());
        prospectiveConnections.add(newConnection);

        // accept connection if the new connection creates higher or equal utility
        return this.getUtility(prospectiveConnections).getOverallUtility() >= this.getUtility().getOverallUtility();
    }

}

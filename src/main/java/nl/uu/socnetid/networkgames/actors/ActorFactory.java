package nl.uu.socnetid.networkgames.actors;

import java.util.concurrent.atomic.AtomicLong;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public final class ActorFactory {

    // counter for unique identifiers
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    // risk neutral risk factor
    protected static final double RISK_NEUTRAL = 1.0;


    /**
     * Factory method to create a new {@link Actor} instance with a custom risk factor.
     *
     * @param network
     *          the {@link Network} the actor is being a part of
     * @param utilityFunction
     *          the {@link UtilityFunction} the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the {@link DiseaseSpecs} characteristics that is or might become present in the network
     * @param riskFactor
     *          the risk factor of a actor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @return the new {@link Actor} instance
     */
    public static Actor createActor(Network network, UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs,
            double riskFactor) {
        return new Actor(String.valueOf(NEXT_ID.getAndIncrement()), network, utilityFunction,
                diseaseSpecs, riskFactor);
    }

    /**
     * Factory method returning a new {@link Actor} instance.
     *
     * @param network
     *          the {@link Network} the actor acts as node in
     * @param utilityFunction
     *          the {@link UtilityFunction} the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the {@link DiseaseSpecs} that is or might become present in the network
     * @return the new {@link Actor} instance
     */
    public static Actor createActor(Network network, UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return ActorFactory.createActor(network, utilityFunction, diseaseSpecs, RISK_NEUTRAL);
    }

    /**
     * Translates the risk factor into interpretable format.
     *
     * @param riskFactor
     *          the risk factor
     * @return interpretable format of risk factor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     */
    public static String getRiskMeaning(double riskFactor) {
        if (riskFactor < 1.0) {
            return "risk seeking";
        } else if (riskFactor > 1.0) {
            return "risk averse";
        }
        return "risk neutral";
    }

}

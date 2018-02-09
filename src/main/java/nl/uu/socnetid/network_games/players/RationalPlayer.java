package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

/**
 * Implementation of a simple {@link Player}.
 *
 * @author Hendrik Nunner
 */
public class RationalPlayer extends AbstractPlayer implements Player {

    // logger
	@SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(RationalPlayer.class);


    /**
     * Private constructor.
     *
     * @param utilityFunction
     *          the function the player uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     */
    protected RationalPlayer(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        super(utilityFunction, diseaseSpecs, RISK_NEUTRAL);
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @param utilityFunction
     *          the function the player uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @return a new {@link Player} instance
     */
    public static Player newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return new RationalPlayer(utilityFunction, diseaseSpecs);
    }


    /**
     * Private constructor.
     *
     * @param utilityFunction
     *          the function the player uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the risk factor of the new player
     */
    protected RationalPlayer(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        super(utilityFunction, diseaseSpecs, riskFactor);
    }

    /**
     * Factory method returning a new {@link Player} instance with a custom risk factor.
     *
     * @param utilityFunction
     *          the function the player uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the custom risk factor
     * @return a new {@link Player} instance with a custom risk factor
     */
    public static Player newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        return new RationalPlayer(utilityFunction, diseaseSpecs, riskFactor);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToCreateConnection()
     */
    @Override
    public Player seekNewConnection() {
        Player potentialConnection = getRandomNotYetConnectedPlayer();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.add(potentialConnection);

        // connect if new connection creates higher or equal utility
        // TODO is higher OR EQUAL what we want, or only higher?
        if (this.getUtility(potentialConnections) >= this.getUtility()) {
            return potentialConnection;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToBreakConnection()
     */
    @Override
    public Player seekCostlyConnection() {
        Player potentialRemoval = getRandomConnection();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.remove(potentialRemoval);

        // disconnect only if removal creates higher utility
        if (this.getUtility(potentialConnections) > this.getUtility()) {
            return potentialRemoval;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#requestConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean acceptConnection(Player newConnection) {
        List<Player> prospectiveConnections = new ArrayList<Player>(this.getConnections());
        prospectiveConnections.add(newConnection);

        // accept connection if the new connection creates higher or equal utility
        // TODO is higher OR EQUAL what we want, or only higher?
        return this.getUtility(prospectiveConnections) >= this.getUtility();
    }

}

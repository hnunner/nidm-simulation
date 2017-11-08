package nl.uu.socnetid.network_games.players;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;

/**
 * Implementation of a simple {@link Player}.
 *
 * @author Hendrik Nunner
 */
public class SimplePlayer extends AbstractPlayer implements Player {

    /** Logger */
	private final static Logger logger = Logger.getLogger(SimplePlayer.class);


    /**
     * Private constructor.
     */
    private SimplePlayer() {
        this.utilFunc = new CumulativeUtilityFunction();
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @return a new {@link Player} instance
     */
    public static Player newInstance() {
        return new SimplePlayer();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToCreateConnection()
     */
    @Override
    protected Player checkToCreateConnection() {

//        // no more connections possible
//        if (notConnectedTos.isEmpty()) {
//            logger.debug("Cannot connect to anyone else, as there "
//                    + "are no more non-existing connections.");
//            return null;
//        }
//
//        Iterator<Entry<Long, Player>> it = notConnectedTos.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<Long, Player> pair = it.next();
//
//            List<Player> potentialConnectedTos = new LinkedList<Player>(connectedTos.values());
//            Player currPlayer = pair.getValue();
//            potentialConnectedTos.add(currPlayer);
//
//            if (utilFunc.getUtility(potentialConnectedTos) > currUtil) {
//                return currPlayer;
//            }
//        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToBreakConnection()
     */
    @Override
    protected Player checkToBreakConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#requestConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean requestConnection(Player player) {
//        List<Player> potentialConnectedTos = new LinkedList<Player>(connectedTos.values());
//        potentialConnectedTos.add(player);
//        boolean betterOff = utilFunc.getUtility(potentialConnectedTos) > currUtil;
//
//        if (betterOff) {
//            connectedTos.put(player.getId(), player);
//        }
//
//        return betterOff;
        return false;
    }

}

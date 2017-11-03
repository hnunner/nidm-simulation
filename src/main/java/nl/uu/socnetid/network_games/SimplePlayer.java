package nl.uu.socnetid.network_games;

import org.apache.log4j.Logger;

public class SimplePlayer implements Player {
	
	final static Logger logger = Logger.getLogger(SimplePlayer.class);
	

	@Override
	public void performAction() {
		logger.debug("Performing action.");
	}
	
}

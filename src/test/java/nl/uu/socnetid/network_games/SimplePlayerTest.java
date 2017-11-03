package nl.uu.socnetid.network_games;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimplePlayerTest {

	private static Player player;

	@BeforeClass
	public static void initPlayer() {
		player = new SimplePlayer();
	}

	@Before
	public void beforeEachTest() {
		System.out.println("This is executed before each Test");
	}

	@After
	public void afterEachTest() {
		System.out.println("This is exceuted after each Test");
	}

	@Test
	public void testSum() {
		player.performAction();
		assertTrue(true);
	}

	
}

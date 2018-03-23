package app;

import org.newdawn.slick.Color;

public class Config {
	
	/**
	 * Possible configs:
	 * 
	 * Everything circles the sun with regular circular orbits:
	 * The sun needs to be much heavier than everything else. Gravity should be low so other objects aren't pulled toward each other.
	 * Nodes should be fast so they aren't just pulled directly into the sun.
	 * Try to spawn nodes in a position where they will fall into natural orbit. 
	 *  SUN_MASS = 900
	 *  GRAVITY_CONSTANT = 0.6f;
	 *  MASS_TO_SIZE_MULTIPLIER = 0.1f;
	 *  NODE_MASS_CHANGE_RATE = 0.5f;
	 *  NODE_INITIAL_MASS = 0.1f;
	 *  NODE_DEFAULT_VELOCITY = 2.5f;
	 *  
	 * Clusters of big objects:
	 *  - low sun mass
	 *  - high node_initial_mass
	 *  - high gravity
	 */

	// A heavier sun generally needs to be offset by lower gravity 
	public static final float SUN_MASS = 600;
	public static final float NODE_INITIAL_MASS = 0.05f;
	// If sun mass is closer to other object mass, then higher gravity will create more situations where bodies orbit around each other (clusters)
	// Too high and it will cause sling-shotting
	// An alternative to increasing gravity is to increase the relative mass of all objects
	public static final float GRAVITY_CONSTANT = 0.08f;
	public static final float MASS_TO_SIZE_MULTIPLIER = 0.05f;

	public static final float NODE_MASS_CHANGE_RATE = 0.5f;
	// Too slow and new bodies will move directly into the sun, too fast and they will outrun gravity and escape
	public static final float NODE_DEFAULT_VELOCITY = 0.5f;
	
	public static final float MAX_DISTANCE_FROM_SUN = 15000;
	public static final float MAX_NUMBER_BODIES = 100;
	
	public static final Color COLOR_BODY = new Color(210, 235, 240); 
	public static final Color COLOR_SUN = new Color(220, 150, 70);
	public static final Color COLOR_TEXT = Color.white;
	public static final Color COLOR_BACKGROUND = new Color(0, 24, 43);
}

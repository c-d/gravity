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
	public static final float SUN_MASS = 50000;
	public static final float NODE_INITIAL_MASS = 200f;
	// If sun mass is closer to other object mass, then higher gravity will create more situations where bodies orbit around each other (clusters)
	// Too high and it will cause sling-shotting
	// An alternative to increasing gravity is to increase the relative mass of all objects
	public static final float GRAVITY_CONSTANT = 0.07f;
	public static final float MASS_TO_SIZE_MULTIPLIER = 0.01f;

	public static final float NODE_MASS_CHANGE_RATE = 0.5f;
	// Too slow and new bodies will move directly into the sun, too fast and they will outrun gravity and escape
	public static final float NODE_DEFAULT_VELOCITY = 4.2f;
	// Bodies with mass less than this won't have their circles drawn (but labels still will be)
	public static final float NODE_MIN_DRAW_MASS = 50;
	
	// Determines when to 'give up' on searching through a quad tree when calculating gravity.
	// Higher -> less accurate gravity, but should be less computationally expensive (will accept a rougher approximation)
	public static final float GRAVITATIONAL_FUDGE_FACTOR = 1f;
	
	//public static final float MAX_DISTANCE_FROM_SUN = 15000;
	public static final float MAX_NUMBER_BODIES = 100;
	
	public static final Color COLOR_BODY = new Color(210, 235, 240); 
	public static final Color COLOR_SUN = new Color(220, 150, 70);
	public static final Color COLOR_TEXT = Color.white;
	public static final Color COLOR_BACKGROUND = new Color(0, 24, 43);
	
	public static final float QUAD_TREE_LINE_WIDTH_MOD = 0.0005f;
	public static final float QUAD_TREE_LINE_COLOR_MOD = 2f;
	
	// Could probably just be one variable, depends if we will ever have non-square universes
	public static final int UNIVERSE_WIDTH = 60000;
	public static final int UNIVERSE_HEIGHT = 60000;
	// When a quad tree is divided down to minimum size, and new bodies entering the quad will cause a collision, and the smallest
	// of the two bodies will be consumed by the largest. Increasing this should also improve performance.
	public static final int QUAD_TREE_MIN_QUAD_SIZE = 20;
	
	public static final Color COLOR_WHITE = new Color(255, 255, 255);
	public static final boolean DRAW_TRAIL = false;
	
}

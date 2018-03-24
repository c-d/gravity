package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

/**
 * Barnes-Hut tree, recursively dividing space into quadrants.
 * 
 * See {@link https://en.wikipedia.org/wiki/Barnes%E2%80%93Hut_simulation} and 
 * {@link http://mathandcode.com/2016/02/16/quadtree.html}
 * @author chris
 *
 */
public class BHTree {

	private BHTree upperLeft;
	private BHTree upperRight;
	private BHTree lowerRight;
	private BHTree lowerLeft;
	
	private int xs;
	private int xl;
	private int ys;
	private int yl;
	
	private int centerX;
	private int centerY;
	
	private int level;
	
	private Body containedBody;
	
	private float mass;
	// Center of mass
	private float cmx = 0;
	private float cmy = 0;
	
	
	/**
	 * 
	 * @param xSmall	Left x
	 * @param ySmall	Top y
	 * @param xLarge	Right x
	 * @param yLarge	Bottom y
	 */
	private BHTree(int level, int xSmall, int ySmall, int xLarge, int yLarge) {
		centerX = (xSmall + xLarge) / 2;
		centerY = (ySmall + yLarge) / 2;
		xs = xSmall;
		xl = xLarge;
		ys = ySmall;
		yl = yLarge;
		this.level = level;
	}
	
	public static BHTree create(List<Body> bodies, int width, int height) {
		BHTree tree = new BHTree(0, 0, 0, width, height);
		for (Body body : bodies) {
			tree.insert(body);
		}
		return tree;
	}
	
	
	public boolean insert(Body b) {
		/*
		 * Three possible states:
		 * 1. Empty (no body, no subnodes)
		 * 2. Subnodes (4), any of which may be empty (doesn't matter)
		 * 3. Have a body (and no subnodes).
		 */
		if (upperLeft == null) {	// Hasn't been split yet (this may be a leaf node)
			if (containedBody == null) {
				containedBody = b;
				// As a leaf the total mass is exactly the mass of the contained body.
				mass = containedBody.getMass();
				cmx = containedBody.getX() * mass;
				cmx = containedBody.getY() * mass;
				// Return early so mass is not added twice.
				return true;
			}
			else {	// State 3, need to move to state 2.
				if (b.getX() == containedBody.getX() && b.getY() == containedBody.getY()) {
					// Two points in exactly the same position - will infinitely recurse.
					return false;
				} 
				if (xl - xs <= 1 || yl - ys <= 1) {
					// We can't split this tree any further.
					// Will just need to give up (eventually gravity will fling close bodies apart anyway so this will resolve itself).
					// TODO: If gravity requires tree nodes... will we be able to calculate differences between bodies like this?
					return false;
				}
				// BHTree(xLeft, yTop, xRight, yBottom
				upperLeft = new BHTree(level + 1, xs, ys, centerX, centerY);
				upperRight = new BHTree(level + 1, centerX, ys, xl, centerY);
				lowerLeft = new BHTree(level + 1, xs, centerY, centerX, yl);
				lowerRight = new BHTree(level + 1, centerX, centerY, xl, yl);				
				
				Body originalBody = containedBody;
				containedBody = null;
				mass = 0;
				cmx = 0;
				cmy = 0;
				// Now that subnodes have been created, we can fill them with recursion.
				this.insert(originalBody);
				this.insert(b);
			}
		}
		else {
			// State 2. Place the body in the right subtree
			if (b.getX() <= 0 || b.getX() >= xl || b.getY() <= 0 || b.getY() >= yl) {
				// Body falls outside of visible screen.
				// TODO: This needs to be resolved by fixing the co-ordinate system.
				return false; 
			}
			else {
				cmx += b.getX() * b.getMass();
				cmy += b.getX() * b.getMass();
				mass += b.getMass();
				if (b.getX() < centerX) {		// Left
					if (b.getY() < centerY) {	// Upper left
						upperLeft.insert(b);
					}
					else {						// Lower left
						lowerLeft.insert(b);
					}
				}
				else {							// Right
					if (b.getY() < centerY) {
						upperRight.insert(b);	// Upper right
					}
					else {
						lowerRight.insert(b);	// Lower right
					} 
				}
			}
		}
		return true;
	}
	
	public float getMass() {
		if (upperLeft == null) {
			if (containedBody == null) {
				// Empty quad
				return 0f;
			}
			// Quad with no children (but a body)
			else return containedBody.getMass();
		}
		else {
			return upperLeft.getMass() + upperRight.getMass() + lowerLeft.getMass() + lowerRight.getMass();
		}
	}
	
	public Point getCenterOfMass() {
		if (containedBody != null) {
			return new Point(containedBody.getX(), containedBody.getY());
		}
		return new Point(centerX, centerY);
	}
	
	public void draw(Graphics g) {
		if (upperLeft != null) {
			upperLeft.draw(g);
			upperRight.draw(g);
			lowerLeft.draw(g);
			lowerRight.draw(g);
		}
		// TODO: Some concerns about the accuracy of the current method of tracking mass...
		//float retrievedMass = getMass();
		//if (Math.abs(retrievedMass - mass) > 0.01f) {
		//	System.out.println("something bad");
		//}
		if (mass > 0) {	
			Color c = Color.green;
			g.setColor(new Color(c.r, c.g, c.b, (float) mass * Config.QUAD_TREE_LINE_COLOR_MOD));
			g.setLineWidth(mass * Config.QUAD_TREE_LINE_WIDTH_MOD);		
			
			g.draw(new Rectangle(xs, ys, xl - xs , yl - ys));
			g.resetLineWidth();
		}
		/*
		if (level > 0) {
			Point centerOfMass = getCenterOfMass();
			if (centerOfMass != null) {
				Circle centerOfMassCircle = new Circle(centerOfMass.getX(), centerOfMass.getY(), 10);
				g.setColor(Color.orange);
				g.draw(centerOfMassCircle);
				g.setColor(Color.green);
			}
		}
		*/
	}

	public void updateGravity(Body body) {
		if (mass == 0 || (body == containedBody)) { 
			return;
		}
		float x = cmx / mass;
		float y = cmy / mass;
		float size = ((xl - xs) + (yl - ys)) / 2;
		float distance = distanceTo(body, x, y);
		if (size / distance < Config.GRAVITATIONAL_ACCURACY) {
			// Close enough
			updateBodyGravity(body, distance);
		}
		else {
			if (upperLeft == null) {
				// Could also calculate using the actual bodies (rather than the center of mass)
				// ... may be worth looking into
				updateBodyGravity(body, distance);
			}
			else {
				upperLeft.updateGravity(body);
				upperRight.updateGravity(body);
				lowerLeft.updateGravity(body);
				lowerRight.updateGravity(body);
			}
		}
	}
	
	private void updateBodyGravity(Body body, float distance) {
		float gravity = (mass * Config.GRAVITY_CONSTANT) / (distance * distance);
		float angle = (float) Math.atan2(centerY - body.getY(), centerX - body.getX());
		body.enactGravity(gravity, angle);
	}
	
	private float distanceTo(Body body, float x, float y) {
		return (float) Math.sqrt(
				(x - body.getX()) * (x - body.getX()) + 
				(y - body.getY() * (y - body.getY())));
	}

}

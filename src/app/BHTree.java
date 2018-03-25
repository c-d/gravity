package app;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	private Map<Body, Float> gravityHistory;
	
	
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
		gravityHistory = new HashMap<Body, Float>();
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
				cmy = containedBody.getY() * mass;
				if (Float.isNaN(cmx) || Float.isNaN(cmy)) {
					System.out.println("NaN");
				}
				// Return early so mass is not added twice.
				return true;
			}
			else {	// State 3, need to move to state 2.
				if (b.getX() == containedBody.getX() && b.getY() == containedBody.getY()) {
					// Two points in exactly the same position - will infinitely recurse.
					return false;
				} 
				if (xl - xs <= Config.QUAD_TREE_MIN_QUAD_SIZE || yl - ys <= 10) {
					// We can't split this tree any further.
					Universe.notifyCollision(containedBody, b);
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
				cmy += b.getY() * b.getMass();
				if (Float.isNaN(cmx) || Float.isNaN(cmy)) {
					System.out.println("NaN");
				}
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
	
	/**
	 * Draw the previously calculated gravity of other bodies towards THIS node.
	 * @param g
	 */
	public void drawGravity(Graphics g) {
		if (upperLeft != null) {
			upperLeft.drawGravity(g);
			upperRight.drawGravity(g);
			lowerLeft.drawGravity(g);
			lowerRight.drawGravity(g);
		}
		g.setLineWidth(4);
		// We only store the history of bodies that we enacted gravity upon at this level.
		// So we can safely assume every item in this collection should be drawn.
		for (Body body : gravityHistory.keySet()) {
			float gravity = gravityHistory.get(body);
			float alpha = gravity * 1000;
			if (alpha > 0.01) {
				Color newColor = new Color(Config.COLOR_BODY.r, Config.COLOR_BODY.g, Config.COLOR_BODY.b, alpha);
				g.setColor(newColor);
				if (containedBody != null) {
					g.drawLine(body.getX(), body.getY(), containedBody.getX(), containedBody.getY());
				}
				else 
					g.drawLine(body.getX(), body.getY(), centerX, centerY);
			}
		}
		g.resetLineWidth();
	}

	public void updateGravity(Body body) {
		if (mass == 0 || (body == containedBody)) { 
			return;
		}
		float x, y, size;
		if (containedBody != null) {
			x = containedBody.getX();
			y = containedBody.getY();
			size = containedBody.getDiameter();
		}
		else {
			x = cmx / mass;
			y = cmy / mass;
			size = ((xl - xs) + (yl - ys)) / 2;
		}
		float distance = distanceTo(body, x, y);
		if (size / distance < Config.GRAVITATIONAL_FUDGE_FACTOR) {
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
		//float angle = (float) Math.sqrt(Math.pow(centerX - body.getX(), 2) + Math.pow(centerY - body.getY(), 2));
		if (Float.isNaN(gravity) || Float.isNaN(distance)) {
			System.out.println("NaN");
		} 
		body.enactGravity(gravity, angle);
		// Maintain a history of gravities, used for drawing later.
		gravityHistory.put(body, gravity);
	}
	
	private float distanceTo(Body body, float x, float y) {
		float result = (float) Math.sqrt(
				Math.pow(x - body.getX(), 2) + 
				Math.pow(y - body.getY(), 2));
		if (Float.isNaN(result)) {
			System.out.println("NaN");
		}
		return result;
	}

}

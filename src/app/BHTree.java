package app;

import java.util.List;

import org.newdawn.slick.Graphics;
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
	
	private Body containedBody;
	
	
	Point centerOfMass;
	int mass;
	
	/**
	 * 
	 * @param xSmall	Left x
	 * @param ySmall	Top y
	 * @param xLarge	Right x
	 * @param yLarge	Bottom y
	 */
	private BHTree(int xSmall, int ySmall, int xLarge, int yLarge) {
		centerX = (xSmall + xLarge) / 2;
		centerY = (ySmall + yLarge) / 2;
		xs = xSmall;
		xl = xLarge;
		ys = ySmall;
		yl = yLarge;
	}
	
	public static BHTree create(List<Body> bodies, int width, int height) {
		BHTree tree = new BHTree(0, 0, width, height);
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
			}
			else {	// State 3, need to move to state 2.
				if (b.getX() == containedBody.getX() && b.getY() == containedBody.getY()) {
					System.out.println("Two bodies in exactly the same position will lead to infinite recursion"
							+ ", so the second node was not placed in the quad tree.");
					return false;
				}
				upperLeft = new BHTree(xs, 0, centerX, centerY);
				upperRight = new BHTree(centerX, 0, xl, centerY);
				lowerLeft = new BHTree(xs, centerY, centerX, yl);
				lowerRight = new BHTree(centerX, centerY, xl, yl);				
				
				Body originalBody = containedBody;
				containedBody = null;
				// Now that subnodes have been created, we can fill them with recursion.
				this.insert(b);
				this.insert(originalBody);
			}
		}
		else {
			// State 2. Place the body in the right subtree
			if (b.getX() < centerX) {
				if (b.getY() < centerY) {
					upperLeft.insert(b);
				}
				else {
					lowerLeft.insert(b);
				}
			}
			else {
				if (b.getY() < centerY) {
					lowerRight.insert(b);
				}
				else {
					lowerRight.insert(b);
				} 
			}
		}
		return true;
	}
	
	public void draw(Graphics g) {
		if (upperLeft != null) {
			upperLeft.draw(g);
			upperRight.draw(g);
			lowerLeft.draw(g);
			lowerRight.draw(g);
		}
		g.draw(new Rectangle(xs, ys, centerX * 2 , centerY * 2));
	}

}

package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Universe {
	
	public static final int CELL_SIZE = 32;
	private int width;
	private int height;
	
	private static List<Body> bodies;
	private static List<Body> destroyedBodies;
	private static Map<Body, Body> collisions;
	private Body sun;
	private Body selectedBody;
	
	private Random rand = new Random();
	
	private BHTree tree;
	private boolean drawQuadTree = true;
	
	public Universe(int width, int height) {
		this.width = width;
		this.height = height;
		bodies = new ArrayList<Body>();
		destroyedBodies = new ArrayList<Body>();
		sun = new Body("Sol", width / 2, height / 2, 0, 0, Config.SUN_MASS, Config.COLOR_SUN);
		collisions = new HashMap<Body, Body>();
	}

	public Body createRandomBody() {
		Body body = createBody((int) (Math.random() * width), (int) (Math.random()  * height));
		int amount = rand.nextInt(400);
		while (amount-- > 0 )
			body.increaseMass();
		return body;
	}
	
	public Body createBody(int x, int y) {
		Body body = new Body(BodyNames.getName(), x, y, Config.NODE_DEFAULT_VELOCITY, getRandomDirection());
		bodies.add(body);
		return body;
	}
	
	private float getRandomDirection() {
		return rand.nextFloat() * 6;
	}
	
	public Body getBodyAt(int x, int y) {
		if (sun.containsPoint(x, y))
			return sun;
		for (Body b : bodies) {
			if (b.containsPoint(x, y)) {
				selectedBody = b;
				return b;
			}
		}
		return null;
	}

	public void draw(Graphics g) {
		for(Body b1 : bodies) {
			b1.draw(g);
			/*
			for (Body b2 : bodies) {
				if (b1 != b2) {
					drawGravityLineBetweenBodies(g, b1, b2);
				}
			}
			*/
			drawGravityLineBetweenBodies(g, b1, sun);
		}
		if (tree != null)
			tree.drawGravity(g);
		sun.draw(g);
		g.setColor(Config.COLOR_TEXT);
		//g.drawString("Sun mass: " + sun.getMass(), 10, height - 35);
		/*
		if (selectedBody != null) {
			g.drawString(selectedBody.name, 10, height - 115);
			g.drawString("Age: " + selectedBody.age, 10, height - 100);
			g.drawString("Mass: " + selectedBody.getMass(), 10, height - 85);
			g.drawString("Position: " + (int)selectedBody.getX() + "," + (int)selectedBody.getY(), 10, height - 70);
			g.drawString("Velocity:" + (float)selectedBody.velocity.x + "," + selectedBody.velocity.y, 10, height - 55);
		}
		*/
		if (drawQuadTree && tree != null) {
			tree.draw(g);
		}
	}

	private void drawGravityLineBetweenBodies(Graphics g, Body b1, Body b2) {
		// Really only useful for the sun...
		float gravity = b1.getGravityMagnitudeTowardBody(b2);
		float alpha = gravity * 100;
		if (alpha > 0.01) {
			Color newColor = new Color(Config.COLOR_BODY.r, Config.COLOR_BODY.g, Config.COLOR_BODY.b, alpha);
			g.setColor(newColor);
			g.setLineWidth(4);
			
			g.drawLine(b1.getX(), b1.getY(), b2.getX(), b2.getY());
			g.resetLineWidth();
		}
	}
	
	
	public void update() {
		/*
		for (Body b1 : bodies) {
			for (Body b2 : bodies) {
				if (b1 != b2) {
					b1.gravitateToward(b2);
				}
			}
			b1.gravitateToward(sun);
			if (b1.distanceTo(sun) > Config.MAX_DISTANCE_FROM_SUN) {
				destroyedBodies.add(b1);
			}
			b1.update();
		}
		*/
		tree = BHTree.create(bodies, width, height);
		for (Body body : bodies) {
			if (body.getX() <= 0 || body.getX() >= width ||
					body.getY() <= 0 || body.getY() >= height) {
				destroyedBodies.add(body);
				break;
			}
			tree.updateGravity(body);
			body.gravitateToward(sun);
			body.update();
		}
		
		processCollisions();
		
		if (!destroyedBodies.isEmpty()) {
 			bodies.removeAll(destroyedBodies);
			destroyedBodies.clear();
		}
	}
	
	/**
	 * Process the pairs of bodies in external quads.
	 * We don't really care if they're exactly in the same position, the fact that they were in the same external quad is enough.
	 */
	private void processCollisions() {
		List<Body> alreadyProcessed = new ArrayList<Body>();	// Don't want to double-count a destroyed body.
		for (Body b1 : collisions.keySet()) {
			Body b2 = collisions.get(b1);
			if (b1.getMass() > b2.getMass() && !alreadyProcessed.contains(b2)) {
				b1.absorbBody(b2);
				destroyedBodies.add(b2);
			}
			else if (!alreadyProcessed.contains(b1)){
				b2.absorbBody(b1);
				destroyedBodies.add(b1);
			}
		}
		collisions.clear();
	}

	public void deleteBody(Body body) {
		bodies.remove(body);
	}

	public void clearAllBodies() {
		bodies.clear();
	}

	public int getNumberOfBodies() {
		return bodies.size();
	}

	public void toggleDrawQuadTree() {
		drawQuadTree = !drawQuadTree;
	}

	public static void notifyCollision(Body b1, Body b2) {
		collisions.put(b1, b2);
	}

}

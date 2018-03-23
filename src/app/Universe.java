package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Universe {
	
	public static final int CELL_SIZE = 32;
	private static int width;
	private static int height;
	
	private static List<Body> bodies;
	private static List<Body> destroyedBodies;
	private Body sun;
	private Body selectedBody;
	
	private View view;
	private Random rand = new Random();
	
	public class View {
		private int xOffset = 0;
		private int yOffset = 0;
	}
	
	public Universe(int width, int height) {
		Universe.width = width;
		Universe.height = height;
		view = new View();
		bodies = new ArrayList<Body>();
		destroyedBodies = new ArrayList<Body>();
		sun = new Body("Sol", width / 2, height / 2, 0, 0, Config.SUN_MASS, Config.COLOR_SUN);
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
			for (Body b2 : bodies) {
				if (b1 != b2) {
					drawGravityLineBetweenBodies(g, b1, b2);
				}
			}
			drawGravityLineBetweenBodies(g, b1, sun);
		}
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
	}

	private void drawGravityLineBetweenBodies(Graphics g, Body b1, Body b2) {
		float gravity = b1.getGravityMagnitudeTowardBody(b2);
		float alpha = gravity * 100;
		if (alpha > 0.01) {
			Color newColor = new Color(Config.COLOR_BODY.r, Config.COLOR_BODY.g, Config.COLOR_BODY.b, alpha);
			g.setColor(newColor);
			g.setLineWidth(4);
			
			//System.out.println(gravity + " - " + alpha);
			g.drawLine(b1.getX(), b1.getY(), b2.getX(), b2.getY());
			g.resetLineWidth();
		}
	}
	
	public void update() {
		for (Body b1 : bodies) {
			for (Body b2 : bodies) {
				if (b1 != b2) {
					b1.gravitateToward(b2);
					/*if (b1.checkForAbsorption(b2)) {
						destroyedBodies.add(b1);
					}*/
				}
			}
			b1.gravitateToward(sun);
			if (b1.distanceTo(sun) > Config.MAX_DISTANCE_FROM_SUN) {
				destroyedBodies.add(b1);
			}
			b1.update();
		}
		if (!destroyedBodies.isEmpty()) {
 			bodies.removeAll(destroyedBodies);
			destroyedBodies.clear();
		}
	}
	
	public void setFocus(int x, int y) {
		
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

}

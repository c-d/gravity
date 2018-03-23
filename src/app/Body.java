package app;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

public class Body {
	
	private Circle circle;
	
	private float mass = 1f;
	protected Vector2f position;
	protected Vector2f velocity;
	protected String name;
	float age = 0.0f;
	protected Color color;
	
	protected float getX() {
		return circle.getCenterX();
	}
	
	protected float getY() {
		return circle.getCenterY();
	}
	
	protected float getMass() {
		return circle.radius;
	}

	public Body(String name, int x, int y, float speed) {
		this(name, x, y, speed, (float) (Math.random() * Config.NODE_INITIAL_MASS), Config.COLOR_BODY);
	}
	
	public Body(String name, int x, int y, float speed, float mass, Color color) {
		circle = new Circle((float)x, (float)y, mass * Config.MASS_TO_SIZE_MULTIPLIER);
		position = new Vector2f((float)x, (float)y);
		this.velocity = new Vector2f();
		this.setVectorLength(velocity, speed);
		this.setVectorAngle(velocity, (float) -Math.PI); // Up
		this.mass = mass;
		this.name = name;
		this.color = color;
	}

	private void updateMass(float diff) {
		circle = new Circle(circle.getCenterX(), circle.getCenterY(), circle.radius + diff * Config.MASS_TO_SIZE_MULTIPLIER);
		mass += diff;
	}
	
	public void increaseMass() {
		updateMass(Config.NODE_MASS_CHANGE_RATE);
	}
	
	public void decreaseMass() {
		updateMass(-Config.NODE_MASS_CHANGE_RATE);
	}

	public void draw(Graphics g) {
		g.setAntiAlias(true);
		g.setColor(color);
		g.fill(circle);
		g.setAntiAlias(false);
		g.drawString(name, getX() + circle.radius, getY() + circle.radius / 2);
	}
	
	public void update() {
		position.add(velocity);
		circle.setCenterX(position.x);
		circle.setCenterY(position.y);
		age += 0.01;
	}

	public boolean containsPoint(int x, int y) {
		return circle.contains((float)x, (float)y);
	}
	

	public void gravitateToward(Body other) {
		Vector2f gravity = new Vector2f(0, 0);		
		setVectorLength(gravity, getGravityMagnitudeTowardBody(other));
		setVectorAngle(gravity, this.angleTo(other));
		velocity.add(gravity);
	}
	
	public float getGravityMagnitudeTowardBody(Body other) {
 		float distance = distanceTo(other);
		return (other.mass * Config.GRAVITY_CONSTANT) / (distance * distance);
	}
	
	/**
	 * Returns true if this body has been absorbed by other.
	 * @param other
	 * @return
	 */
	public boolean checkForAbsorption(Body other) {
		float distance = distanceTo(other);
 		// If center of this body is contained within other body, check if the entirety is
 		if (distance < other.circle.radius) {
			System.out.println("ABSORBED BY OTHER BODY");
			other.updateMass(mass);
			return true;
 		}
 		return false;
	}
	
	private float angleTo(Body other) {
		return (float) Math.atan2(other.getY() - this.getY(), other.getX() - this.getX());
	}
	
	public float distanceTo(Body other) {
		float dx = other.getX() - this.getX();
		float dy = other.getY() - this.getY();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	private void setVectorAngle(Vector2f vector, float angle) {
		float length = vector.length();
		vector.x = (float) (Math.cos(angle) * length);
		vector.y = (float) (Math.sin(angle) * length);
	}
	
	private void setVectorLength(Vector2f vector, float length) {
		float angle = (float) Math.atan2(vector.y, vector.x);
		vector.x = (float) (Math.cos(angle) * length);
		vector.y = (float) (Math.sin(angle) * length);
	}

}

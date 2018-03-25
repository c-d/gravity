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
	
	public String toString() {
		return name + " [" + getX() + ", " + getY() + "]";
	}
	
	protected float getX() {
		return circle.getCenterX();
	}
	
	protected float getY() {
		return circle.getCenterY();
	}
	
	protected float getMass() {
		return mass;
	}

	public Body(String name, int x, int y, float speed, float direction) {
		this(name, x, y, speed, direction, (float) (Math.random() * Config.NODE_INITIAL_MASS), Config.COLOR_BODY);
	}
	
	public Body(String name, int x, int y, float speed, float direction, float mass, Color color) {
		if (Float.isNaN(x) || Float.isNaN(y)) {
			System.out.println("NaN");
		}
		circle = new Circle((float)x, (float)y, mass * Config.MASS_TO_SIZE_MULTIPLIER);
		position = new Vector2f((float)x, (float)y);
		this.velocity = new Vector2f();
		this.setVectorLength(velocity, speed);
		this.setVectorAngle(velocity, direction); // Up
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
		g.setColor(color);
		if (mass > Config.NODE_MIN_DRAW_MASS) {
			g.setAntiAlias(true);
			g.fill(circle);
			
		}
		g.drawString(name, getX() + circle.radius, getY() + circle.radius / 2);
		
		// Draw velocity
		Vector2f projectedPosition = new Vector2f(getX(), getY());
		projectedPosition = projectedPosition.add(velocity);
		//setVectorLength(projectedPosition, 1);
		g.drawLine(getX(), getY(), projectedPosition.getX(), projectedPosition.getY());
	}
	
	public void update() {
 		position.add(velocity);
		if (Float.isNaN(position.x) || Float.isNaN(position.y)) {
			System.out.println("NaN");
		}
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
	
	public void enactGravity(float gravityMagnitude, float angle) {
		if (Float.isNaN(gravityMagnitude) || Float.isNaN(angle)) {
			System.out.println("NaN");
		}
		Vector2f g = new Vector2f(0, 0);		
		setVectorLength(g, gravityMagnitude);
		setVectorAngle(g, angle);
		velocity.add(g);
	}
	
	public float getGravityMagnitudeTowardBody(Body other) {
 		float distance = distanceTo(other);
		return (other.mass * Config.GRAVITY_CONSTANT) / (distance * distance);
	}
	

	public void absorbBody(Body other) {
		System.out.println(name + " absorbed " + other.name + " and gained " + other.getMass() + " mass!");
		updateMass(other.getMass());
	}
	
	private float angleTo(Body other) {
		//float sqrt = (float) Math.sqrt(Math.pow(other.getX() - getX(), 2) + Math.pow(other.getY() - getY(), 2));
		float atan = (float) Math.atan2(other.getY() - this.getY(), other.getX() - this.getX());
		return atan;
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

	public float getDiameter() {
		return circle.getRadius() * 2;
	}


}

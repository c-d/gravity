package app;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame {

	private static int screenWidth = 1280;
	private static int screenHeight = 900;
	private Universe world;
	private boolean pause = true;
	private float zoomLevel = 1;
	
	public Game(String title) {
		super(title);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		//bg.draw(0, 0, screenWidth, screenHeight);
		g.setBackground(Config.COLOR_BACKGROUND);
		// This does not function well with input (mouse co-ords not translated)
		g.translate((screenWidth / 2) - (screenWidth / 2) * zoomLevel, (screenHeight / 2) - (screenHeight / 2) * zoomLevel);
		g.scale(zoomLevel, zoomLevel);
		world.draw(g);
		g.resetTransform();
		g.drawString("Bodies: " + world.getNumberOfBodies(), 10, screenHeight - 20);
		g.drawString("Zoom: " + (int)(zoomLevel * 100) + "%", 10, screenHeight - 35);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		BodyNames.init();
		world = new Universe(screenWidth, screenHeight);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		processInput(gc, delta);
		if (!pause)
			stepUpdate(gc, delta);
	}
	
	private void stepUpdate(GameContainer gc, int delta) {
		world.update();
	}
	
	private void processInput(GameContainer gc, int delta) {
		Input input = gc.getInput();
		Body node = world.getBodyAt(input.getMouseX(), input.getMouseY());
		
		// Mouse clicks
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			if (node == null) {
				world.createBody(input.getMouseX(), input.getMouseY());
			}
			else node.increaseMass();
		}
		if (input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			if (node != null) {
				node.decreaseMass();
			}
		}
		// Body manipulation
		if (input.isKeyDown(Input.KEY_D)) {
			if (node != null) {
				world.deleteBody(node);
			}
		}
		if (input.isKeyDown(Input.KEY_C)) {
			world.clearAllBodies();
		}
		if (input.isKeyPressed(Input.KEY_R)) {
			world.createRandomBody();
		}
		
		// UI manipulation
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			pause = !pause;
		}
		if (pause && input.isKeyDown(Input.KEY_RIGHT)) {
			stepUpdate(gc, delta);
		}
		// Zoom events
		if (input.isKeyDown(Input.KEY_DOWN) && zoomLevel > 0) {
			zoomLevel -= 0.01;
			System.out.println("New zoom level: " + zoomLevel);
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			zoomLevel += 0.01;
			System.out.println("New zoom level: " + zoomLevel);
		}
		if (input.isKeyDown(Input.KEY_RCONTROL)) {
			zoomLevel = 1;
			System.out.println("New zoom level: " + zoomLevel);
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appGC = new AppGameContainer(new Game("G"));
			appGC.setDisplayMode(screenWidth, screenHeight, false);
			appGC.start();
		}
		catch (SlickException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "Failed to initialise game.", ex);
		}
	}

}

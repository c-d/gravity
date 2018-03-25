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
		//g.translate(screenWidth / zoomLevel / 2, 
		//		screenHeight / zoomLevel / 2);
		g.translate((screenWidth / 2) - (Config.UNIVERSE_WIDTH / 2) * zoomLevel, (screenHeight / 2) - (Config.UNIVERSE_HEIGHT / 2) * zoomLevel);
		g.scale(zoomLevel, zoomLevel);
		world.draw(g);
		g.resetTransform();
		g.drawString("Bodies: " + world.getNumberOfBodies(), 10, screenHeight - 20);
		g.drawString("Zoom: " + (int)(zoomLevel * 100) + "%", 10, screenHeight - 35);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		BodyNames.init();
		world = new Universe(Config.UNIVERSE_WIDTH, Config.UNIVERSE_HEIGHT);
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
	
	private int getMouseX(Input input) {
		//int result = (int) ((input.getMouseX() + ((Config.UNIVERSE_WIDTH - screenWidth)) / 2));
		//result = (int) (result);
		//System.out.println("X: " + input.getMouseX() + ", " + result + "    Screen-offset: " + ((Config.UNIVERSE_WIDTH - screenWidth) * zoomLevel / 2));
		//return (int) result; 
		return (int) ((input.getMouseX() + (Config.UNIVERSE_WIDTH - screenWidth) / 2)); 
	}	
	
	private int getMouseY(Input input) {
		return (int) ((input.getMouseY() + (Config.UNIVERSE_HEIGHT - screenHeight) / 2)); 
	}
	
	private void processInput(GameContainer gc, int delta) {
		Input input = gc.getInput();
		Body node = world.getBodyAt(getMouseX(input), getMouseY(input));
		
		// Mouse clicks
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			if (node == null) {
				world.createBody(getMouseX(input), getMouseY(input));
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
		if (input.isKeyDown(Input.KEY_T)) {
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
		if (input.isKeyDown(Input.KEY_DOWN) && zoomLevel > 0.015) {
			zoomLevel -= 0.001;
			System.out.println("New zoom level: " + zoomLevel);
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			zoomLevel += 0.001;
			System.out.println("New zoom level: " + zoomLevel);
		}
		if (input.isKeyDown(Input.KEY_RCONTROL)) {
			zoomLevel = 1;
			System.out.println("New zoom level: " + zoomLevel);
		}
		if (input.isKeyPressed(Input.KEY_Q)) {
			world.toggleDrawQuadTree();
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appGC = new AppGameContainer(new Game("Gravity"));
			appGC.setDisplayMode(screenWidth, screenHeight, false);
			appGC.start();
		}
		catch (SlickException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "Failed to initialise game.", ex);
		}
	}

}

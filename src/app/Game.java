package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame {

	private static int screenWidth = 1280;
	private static int screenHeight = 900;
	private Universe world;
	private Image bg;
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
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		BodyNames.init();
		world = new Universe(screenWidth, screenHeight);
		//bg = new Image("res/basepack/bg.png");
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
		boolean change = false;
		Body node = world.getBodyAt(input.getMouseX(), input.getMouseY());
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			if (node == null) {
				world.createBody(input.getMouseX(), input.getMouseY());
			}
			else node.increaseMass();
			change = true;
		}
		if (input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			if (node != null) {
				node.decreaseMass();
			}
			change = true;
		}
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
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			pause = !pause;
		}
		if (pause && input.isKeyDown(Input.KEY_RIGHT)) {
			stepUpdate(gc, delta);
		}
		// Zoom events
		if (input.isKeyDown(Input.KEY_DOWN)) {
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
		if (change)
			world.setFocus(input.getMouseX(), input.getMouseY());
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appGC = new AppGameContainer(new Game("New game"));
			appGC.setDisplayMode(screenWidth, screenHeight, false);
			appGC.start();
		}
		catch (SlickException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "Failed to initialise game.", ex);
		}
	}

}

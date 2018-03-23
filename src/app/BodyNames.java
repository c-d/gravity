package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class BodyNames {
	
	private static List<String> names;
	private static Iterator<String> it;

	public static void init() {
		names = readPlanetNames("res/planet-names.txt");
		shuffle();
	}
	
	private static void shuffle() {
		Collections.shuffle(names);
		it = names.iterator();
	}
	
	public static String getName() {
		if (!it.hasNext()) {
			shuffle();
		}
		return it.next();
	}
	
	private static List<String> readPlanetNames(String path) {
		List<String> results = new ArrayList<String>();
		File file = new File(path);
		Scanner input = null;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (input.hasNextLine()) {
			results.add(input.nextLine());
		}
		return results;
	}

}

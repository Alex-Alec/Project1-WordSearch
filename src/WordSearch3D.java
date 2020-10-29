import java.util.Random;
import java.io.*;

/**
 * Implements a 3-d word search puzzle program.
 */
public class WordSearch3D {
	public WordSearch3D () {
	}

	/**
	 * Searches for all the words in the specified list in the specified grid.
	 * You should not need to modify this method.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param words the words to search for
	 *
	 */
	public int[][][] searchForAll (char[][][] grid, String[] words) {
		final int[][][] locations = new int[words.length][][];
		for (int i = 0; i < words.length; i++) {
			locations[i] = search(grid, words[i]);
		}
		return locations;
	}

	/**
	 * Searches for the specified word in the specified grid.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param word the word to search for
	 * @return If the grid contains the
	 * word, then the method returns a list of the (3-d) locations of its letters; if not, 
	 */
	public int[][] search (char[][][] grid, String word) {

		// Loop through every point in the word search
		for(int i = 0;i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				for(int k = 0; k < grid[0][0].length; k++){

					// Check if the value at the current position matches the first letter of the word
					if(grid[i][j][k] == word.charAt(0)){

						// If the first letter matches, search in all 26 directions for the word
						int[][] result = loopThroughAllDirections(grid, word, i, j, k);

						// If the result comes back not null, then the word was found
						if(result != null){
							return result;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Loops through all 26 direction from a starting point & searches for the word in each of those directions.
	 * @param grid the grid of character in the word search
	 * @param word the word being searching for
	 * @param xStart x coordinate of the starting position
	 * @param yStart y coordinate of the starting position
	 * @param zStart z coordinate of the starting position
	 * @return if this starting value leads to the word, then return the position of the word in a list
	 */
	private int[][] loopThroughAllDirections(char[][][] grid, String word, int xStart, int yStart, int zStart){

		// Loops through the 26 possible directions to travel from a point in the grid
		for(int xDir = -1; xDir < 2; xDir++){
			for(int yDir = -1; yDir < 2; yDir++){
				for(int zDir = -1; zDir < 2; zDir++){

					// If the direction is invalid, then skip
					if(xDir == 0 && yDir == 0 && zDir == 0){
						continue;
					}

					// Checks for the word in the current direction
					int[][] result = checkDirection(grid, word, xStart, yStart, zStart, xDir, yDir, zDir);

					// If the result is non-null, than we have located a valid result
					if(result != null){
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Checks for the existence of the word from a starting point and direction in the grid
	 * @param grid the grid of character in the word search
	 * @param word the word being searched for
	 * @param xStart starting x position
	 * @param yStart starting y position
	 * @param zStart starting z position
	 * @param xDir x Direction {-1, 0, 1}
	 * @param yDir y Direction {-1, 0, 1}
	 * @param zDir z Direction {-1, 0, 1}
	 * @return if this starting value leads to the word, then return the position of the word in a list
	 */
	private int[][] checkDirection(char[][][] grid, String word, int xStart, int yStart, int zStart, int xDir, int yDir, int zDir){

		// Create a result array
		int[][] result = new int[word.length()][3];

		// Loop through the characters in the word & check if the next letter in the direction continues to match the word
		for(int i = 0; i < word.length(); i++) {

			// Current position based on start position and direction
			int xCurrent = xStart + i * xDir;
			int yCurrent = yStart + i * yDir;
			int zCurrent = zStart + i * zDir;

			// Return if out of bounds
			if(!isValidPosition(grid, xCurrent, yCurrent, zCurrent)) {
				return null;
			}

			// Return is the letter doesn't match
			if(grid[xCurrent][yCurrent][zCurrent] != word.charAt(i)){
				return null;
			}

			// If in bounds & the letter matches, set the corresponding value in the result array to the correct location
			result[i][0] = xCurrent;
			result[i][1] = yCurrent;
			result[i][2] = zCurrent;
		}

		return result;
	}

	/**
	 * Checks if the position is in bounds of the grid
	 * @param grid the grid used
	 * @param x the x position
	 * @param y the y position
	 * @param z the z position
	 * @return if the x,y,z coordinate is in bounds of the grid
	 */
	private boolean isValidPosition(char[][][] grid, int x, int y, int z){

		// Check if any of the coordinates are out of bounds
		if(x < 0 || x >=  grid.length){
			return false;
		} else if(y < 0 || y >=  grid[0].length){
			return false;
		} else if(z < 0 || z >=  grid[0][0].length) {
			return false;
		}
		return true;
	}

	/**
	 * Tries to create a word search puzzle of the specified size with the specified
	 * list of words.
	 * @param words the list of words to embed in the grid
	 * @param sizeX size of the grid along first dimension
	 * @param sizeY size of the grid along second dimension
	 * @param sizeZ size of the grid along third dimension
	 * @return a 3-d char array if successful that contains all the words, or <tt>null</tt> if
	 * no satisfying grid could be found.
	 */
	public char[][][] make(String[] words, int sizeX, int sizeY, int sizeZ) {

		// Confirms that the inputs from the user are valid
		if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0){
			return null;
		}

		// Create the wordSearch Grid
		char[][][] wordSearch = new char[sizeX][sizeY][sizeZ];

		// Create rng
		final Random rng = new Random();

		// Stores amount of attempts used to generate a grid using the word list
		int overallCreationAttempts = 0;

		// Confirm that none of the words are impossible to place in the grid alone (too long)
		if(!checkFitWithinGrid(wordSearch, words)) {
			return null;
		}

		// Loop through each word and attempt to place it in the grid
		for(int i = 0; i < words.length; i++){

			// If you failed to place the word after 1000 * 26 attempts, the restart the grid from scratch
			// Max Attempts: Need to try 26 different directions per starting points, so to attempt 1000 starting points need about 26000 to cover most of them
			if(!attemptPlacingWord(wordSearch, words[i], 1000 * 26, rng)){

				// Reset & increment amount of creation attempts
				i = 0;
				wordSearch = new char[sizeX][sizeY][sizeZ];
				overallCreationAttempts++;

				// If after 1000 attempts, no valid grid has been created, then return null
				if(overallCreationAttempts>=1000){
					return null;
				}
			}
		}

		// Fill the grid with random chars
		fillGrid(wordSearch, rng);

		// Debug Statement: printGrid(wordSearch);

		return wordSearch;
	}

	/**
	 * Generate random coordinates based on the size parameters
	 * @param rng random
	 * @param sizeX size of the grid in the x direction
	 * @param sizeY size of the grid in the y direction
	 * @param sizeZ size of the grid in the z direction
	 * @return an int[] with the randomly generated coordinate
	 */
	private int[] generateRandomCoordinate(Random rng, int sizeX, int sizeY, int sizeZ){

		int[] result = new int[3];

		result[0] = rng.nextInt(sizeX);
		result[1] = rng.nextInt(sizeY);
		result[2] = rng.nextInt(sizeZ);

		return result;
	}

	/**
	 * Generate a random direction
	 * @param rng
	 * @return a list of the random direction
	 */
	private int[] generateRandomDirection(Random rng){

		int[] result = new int[3];

		result[0] = rng.nextInt(3) - 1; // {-1, 0, 1}
		result[1] = rng.nextInt(3) - 1; // {-1, 0, 1}
		result[2] = rng.nextInt(3) - 1; // {-1, 0, 1}

		return result;
	}

	/**
	 * Checks if all  the words can fit in the grid
	 * @param grid word search being worked on
	 * @param words list of all words that need to be placed
	 * @return whether the all words can fit in the grid
	 */
	private boolean checkFitWithinGrid(char[][][] grid, String[] words){
		for(int i = 0; i < words.length; i++) {
			if (words[i].length() > Math.max(Math.max(grid.length, grid[0].length), grid[0][0].length)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prints the grid for debugging purposes
	 * @param grid the printed grid
	 */
	private void printGrid(char[][][] grid){
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				for(int k = 0; k < grid[0][0].length;k++){
					if(grid[i][j][k] == '\u0000'){
						System.out.print(".");
					}else {
						System.out.print(grid[i][j][k]);
					}
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	/**
	 * Fills untouched parts of the grid with random characters
	 * @param grid grid that needs to be filled
	 * @param rng
	 */
	private void fillGrid(char[][][] grid, Random rng){
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				for(int k = 0; k < grid[0][0].length;k++){
					if(grid[i][j][k] == '\u0000') {
						grid[i][j][k] = (char) (rng.nextInt(26) + 'a');
					}
				}
			}
		}
	}

	/**
	 * Attempts placing a word in a grid a given amount of times
	 * @param wordSearch grid used to place words into
	 * @param word the word attempted to be placed
	 * @param maxAttempts maximum number of attempts before giving up
	 * @param rng
	 * @return whether it successfully placed the word (true) or gave up (false)
	 */
	private boolean attemptPlacingWord(char[][][] wordSearch, String word, int maxAttempts, Random rng){

		// Instantiate variables for use in the while loop
		boolean success = false;
		int failCounter = 0;

		int[] newRandomCoordinate;
		int[] newRandomDirection;

		// Loop runs until the word is successfully placed or the word fails to be placed 10000 times
		while(!success && failCounter < maxAttempts){

			// Generate a random coordinate and direction
			newRandomCoordinate = generateRandomCoordinate(rng, wordSearch.length, wordSearch[0].length, wordSearch[0][0].length);
			newRandomDirection = generateRandomDirection(rng);

			// Attempt to place the word in the grid
			success = placeWordInGrid(wordSearch, word, newRandomCoordinate, newRandomDirection);

			// Increment the fail counter
			failCounter++;
		}

		// Return if it accomplished the task successfully
		return failCounter < maxAttempts;
	}

	/**
	 * Place a word in the grid based on a start coordinate and a direction
	 * @param grid
	 * @param word
	 * @param startCoordinate
	 * @param direction
	 * @return whether the task was successfully accomplished
	 */
	private boolean placeWordInGrid(char[][][] grid, String word, int[] startCoordinate, int[] direction){

		int startX = startCoordinate[0];
		int startY = startCoordinate[1];
		int startZ = startCoordinate[2];

		int dirX = direction[0];
		int dirY = direction[1];
		int dirZ = direction[2];

		// Make sure the direction is valid
		if(dirX == 0 && dirY == 0 && dirZ == 0){
			return false;
		}

		// Loop through each character in the word
		for(int i = 0; i < word.length(); i++){

			// Set current positions based on start and direction.
			int currentX = startX + i * dirX;
			int currentY = startY + i * dirY;
			int currentZ = startZ + i * dirZ;

			// Makes sure the position that a character would be placed is in bounds
			if(!isValidPosition(grid, currentX, currentY, currentZ)){
				return false;
			}

			// Makes sure that if a letter already exists where the program wants to place a character that it is the same character
			if(grid[currentX][currentY][currentZ] != '\u0000' && grid[currentX][currentY][currentZ] != word.charAt(i)){
				return false;
			}
		}

		// If every character would be placed at a valid location then add it to the grid
		for(int i = 0; i < word.length(); i++){
			grid[startX + i * dirX][startY + i * dirY][startZ + i * dirZ] = word.charAt(i);
		}

		// Debug Statement: printGrid(grid);

		return true;
	}

	/**
	 * Exports to a file the list of lists of 3-d coordinates.
	 * You should not need to modify this method.
	 * @param locations a list (for all the words) of lists (for the letters of each word) of 3-d coordinates.
	 * @param filename what to name the exported file.
	 */
	public static void exportLocations (int[][][] locations, String filename) {
		// First determine how many non-null locations we have
		int numLocations = 0;
		for (int i = 0; i < locations.length; i++) {
			if (locations[i] != null) {
				numLocations++;
			}
		}

		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(numLocations);  // number of words
			pw.print('\n');
			for (int i = 0; i < locations.length; i++) {
				if (locations[i] != null) {
					pw.print(locations[i].length);  // number of characters in the word
					pw.print('\n');
					for (int j = 0; j < locations[i].length; j++) {
						for (int k = 0; k < 3; k++) {  // 3-d coordinates
							pw.print(locations[i][j][k]);
							pw.print(' ');
						}
					}
					pw.print('\n');
				}
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Exports to a file the contents of a 3-d grid.
	 * You should not need to modify this method.
	 * @param grid a 3-d grid of characters
	 * @param filename what to name the exported file.
	 */
	public static void exportGrid (char[][][] grid, String filename) {
		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(grid.length);  // height
			pw.print(' ');
			pw.print(grid[0].length);  // width
			pw.print(' ');
			pw.print(grid[0][0].length);  // depth
			pw.print('\n');
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[0].length; y++) {
					for (int z = 0; z < grid[0][0].length; z++) {
						pw.print(grid[x][y][z]);
						pw.print(' ');
					}
				}
				pw.print('\n');
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Creates a 3-d word search puzzle with some nicely chosen fruits and vegetables,
	 * and then exports the resulting puzzle and its solution to grid.txt and locations.txt
	 * files.
	 */
	public static void main (String[] args) {
		final WordSearch3D wordSearch = new WordSearch3D();
		final String[] words = new String[] { "apple" , "orange", "pear", "peach", "durian", "lemon", "lime", "jackfruit", "plum", "grape", "apricot", "blueberry", "tangerine", "coconut", "mango", "lychee", "guava", "strawberry", "kiwi", "kumquat", "persimmon", "papaya", "longan", "eggplant", "cucumber", "tomato", "zucchini", "olive", "pea", "pumpkin", "cherry", "date", "nectarine", "breadfruit", "sapodilla", "rowan", "quince", "toyon", "sorb", "medlar" };
		final int xSize = 10, ySize = 10, zSize = 10;
 		final char[][][] grid = wordSearch.make(words, xSize, ySize, zSize);
		exportGrid(grid, "grid.txt");

		final int[][][] locations = wordSearch.searchForAll(grid, words);
		exportLocations(locations, "locations.txt");

	}
}

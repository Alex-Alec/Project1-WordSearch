import java.util.Random;
import java.util.*;
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
	 * @param a list of lists of locations of the letters in the words
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
		// TODO: implement me
		for(int i = 0;i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				for(int k = 0; k < grid[0][0].length;k++){
					if(grid[i][j][k] == word.charAt(0)){
						for(int a = -1; a < 2; a++){
							for(int b = -1; b < 2; b++){
								for(int c = -1; c < 2; c++){
									if(a == 0 && b == 0 && c == 0){
										continue;
									}
									int[][] result = checkDirection(grid, word, i, j, k, a, b, c);
									if(result != null){
										return result;
									}
								}
							}
						}

					}
				}
			}
		}
		return null;
	}

	// a, b, c {-1, 0, 1} specify movement in that direction
	//a is up down, b is left right, and c is front back
	//a is positive, goes down, b is positive go right, c is positive go into page (back)
	private int[][] checkDirection(char[][][] grid, String word, int iCurrent, int jCurrent, int kCurrent, int a, int b, int c){
		int[][] result = new int[word.length()][3];

		for(int i = 1; i < word.length(); i++) {
			if(!isValidPosition(grid, iCurrent+  i*a, jCurrent + i * b, kCurrent + i*c)) {
				return null;
			}
			if(grid[iCurrent+  i*a][jCurrent + i * b][kCurrent + i*c] != word.charAt(i)){
				return null;
			}
			result[i][0] = iCurrent + i*a;
			result[i][1] = jCurrent + i * b;
			result[i][2] = kCurrent + i*c;
		}
		result[0][0] = iCurrent;
		result[0][1] = jCurrent;
		result[0][2] = kCurrent;
		return result;
	}

	private boolean isValidPosition(char[][][] grid, int i , int j , int k){
		if(i < 0 || i >=  grid.length){
			return false;
		} else if(j < 0 || j >=  grid.length){
			return false;
		} else if(k < 0 || k >=  grid.length) {
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
	public char[][][] make (String[] words, int sizeX, int sizeY, int sizeZ) {
		// TODO: implement me
		return null;
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
		final String[] words = new String[] { "apple", "orange", "pear", "peach", "durian", "lemon", "lime", "jackfruit", "plum", "grape", "apricot", "blueberry", "tangerine", "coconut", "mango", "lychee", "guava", "strawberry", "kiwi", "kumquat", "persimmon", "papaya", "longan", "eggplant", "cucumber", "tomato", "zucchini", "olive", "pea", "pumpkin", "cherry", "date", "nectarine", "breadfruit", "sapodilla", "rowan", "quince", "toyon", "sorb", "medlar" };
		final int xSize = 10, ySize = 10, zSize = 10;
		final char[][][] grid = wordSearch.make(words, xSize, ySize, zSize);
		exportGrid(grid, "grid.txt");

		final int[][][] locations = wordSearch.searchForAll(grid, words);
		exportLocations(locations, "locations.txt");
	}
}

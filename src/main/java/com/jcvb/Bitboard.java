package com.jcvb;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3x3 Tic Tac Toe board. <p>
 * Offers methods to manipulate the board and check for a winner. <p>
 * The board is represented as two 16-bit integer.
 */
public class Bitboard {
	
	/**
	 * This represents a 3*3 board. Each char represents the state for each player.
	 * <p>
	 * Bit representation: <p>
	 *  0 | 1 | 2 <p>
	 *  --------- <p>
	 *  7 | 8 | 3 <p>
	 *  --------- <p>
	 *  6 | 5 | 4 <p>
	 * <p>
	 *  Human-readable representation: <p>
	 *  0 | 1 | 2 <p>
	 *  --------- <p>
	 *  3 | 4 | 5 <p>
	 *  --------- <p>
	 *  6 | 7 | 8 <p>
	 */
	private final char[] board = new char[2];
	
	/**
	 * The current game status.
	 */
	private GameStatus gameStatus = GameStatus.RUNNING;
	
	/**
	 * A unique identifier for the board. <p>
	 * Used to save where a board is located <p>
	 * Needed to the moves of the board <p>
	 * TODO: Needs to be provided to the constructor
	 */
	private int uniqueId = 0;
	
	/**
	 * Creates a new Bitboard with an empty board.
	 */
	public Bitboard(int uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	/**
	 * Clones an existing Bitboard.
	 * @param parent The Bitboard to clone.
	 */
	public Bitboard(Bitboard parent) {
		this.board[0] = parent.extractChar(0);
		this.board[1] = parent.extractChar(1);
		this.gameStatus = parent.getGameStatus();
		this.uniqueId = parent.getUniqueId();
		
	}
	
	private int getUniqueId() {
		return uniqueId;
	}
	
	/**
	 * Returns the current game status.
	 * @return The current game status.
	 */
	public GameStatus getGameStatus() {
		return gameStatus;
	}
	
//	public List<Integer> getPossibleMovesPartialDeduplication() {
//		char occupied = (char) (board[0] | board[1]);
//
//		if (occupied == 0) {
//			return List.of(0,1,4);
//		} else {
//			return getPossibleMoves();
//		}
//	}
	
	/**
	 * Returns the possible moves for the current board.
	 * TODO: Make dependent on the uniqueId
	 * TODO: Remove symmetries from the possible moves
	 *
	 * @return An array of integers representing the possible moves.
	 */
	public List<Integer> getPossibleMoves(ArrayList<Integer> list) {
		// Empty fields remain unset.
		char occupied = (char) (board[0] | board[1]);
		
		for (int i = 0; i < 9; i++) {
			char mask = (char) (1 << i);
			char result = (char) (occupied & mask);
			
			if (result == 0) {
				list.add(fromBitToHuman(i) + (uniqueId * 9));
			}
		}
		
		// Possible solution to symmetry deduplication:
		// Check if rotating and flipping a possible move results in a move that is already in the list
		
		return list;
	}
	
	/**
	 * Returns the possible moves for the current board without symmetries.
	 * @param player The player to get the possible moves for.
	 * @return A List of integers representing the possible moves.
	 */
	public List<Integer> getPossibleMovesWithoutSymmetries(int player) {
		// Empty fields remain unset.
		char occupied = (char) (board[0] | board[1]);
		
		List<Integer> positions = new ArrayList<>(9-countSetBits(occupied));
		
		for (int i = 0; i < 9; i++) {
			char mask = (char) (1 << i);
			char result = (char) (occupied & mask);
			
			if (result == 0) {
				positions.add(fromBitToHuman(i));
			}
		}
		
		// Iterates over all possible moves and removes symmetrical ones
		for (int i = 0; i < positions.size(); i++) {
			for (int j = i + 1; j < positions.size(); j++) {
				
				// Create two temporary bitboards to check for symmetry
				// Original board is not modified
				Bitboard temp1 = new Bitboard(this);
				Bitboard temp2 = new Bitboard(this);
				
				// Apply the move to the temporary bitboards
				temp1.set(positions.get(i), player);
				temp2.set(positions.get(j), player);
				
				// Check if the two bitboards are symmetrical
				if (areSymmetrical(temp1, temp2)) {
					positions.remove(j);
					j--;
				}
			}
		}
		
		return positions;
	}
	
	/**
	 * Checks if two bitboards are symmetrical. <p>
	 * The bitboards are considered symmetrical if they are equal after rotating and flipping. <p>
	 * The bitboards are not modified.
	 * @param board1 The first bitboard to check.
	 * @param board2 The second bitboard to check.
	 * @return True if the bitboards are symmetrical, false otherwise.
	 */
	public boolean areSymmetrical(Bitboard board1, Bitboard board2) {
		for (int i = 0; i < 4; i++) {
			Bitboard temp1 = new Bitboard(board1);
			Bitboard temp2 = new Bitboard(board2);
			
			temp1.rotateRight(i);
			
			if (temp1.equals(temp2)) {
				return true;
			}
			
			temp1.flipHorizontally();
			
			if (temp1.equals(temp2)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getPositionsSetCount(int player) {
		return countSetBits(board[player]);
	}
	
	/**
	 * Counts the number of set bits in an unsigned 32-bit integer.
	 * Uses a trick to save some operations.
	 * Doesn't use a loop.
	 *
	 * @see <a href="https://tekpool.wordpress.com/category/bit-count/">Source and Explanation</a>
	 * @param u The integer to count the set bits of.
	 * @return The number of set bits in the integer.
	 */
	private int countSetBits(int u) {
		int ucount = u - ((u >>> 1) & 033333333333) - ((u >>> 2) & 011111111111);
		
		return ((ucount + (ucount >>> 3)) & 030707070707) % 63;
	}
	
	public void set(Position position, int player) {
		this.set(position.ordinal(), player);
	}
	
	/**
	 * Sets the board at the given index to the given player.
	 * @param index The index in human-readable format.
	 * @param player The player to set the index to.
	 */
	public void set(int index, int player) {
		if (index < 0 || index > 8) {
			System.err.println("Index " + index + " out of bounds");
			return;
		}
		
		if (player < 0 || player > 1) {
			System.err.println("Player " + player + " out of bounds");
			return;
		}
		
		int translatedIndex = fromHumanToBit(index);
		
		// Set the bit at the given index via a bitwise OR operation
		board[player] |= (char) (1 << translatedIndex);
	}
	
	/**
	 * <b>FOR DEBUG PURPOSES ONLY!</b> <p>
	 * Used to bring the board into a specific state. <p>
	 * Adds the given string to the current board state.
	 * @param addition The string to set the board to.
	 * @see Bitboard#fromString(String)
	 */
	public void set(String addition) {
		if (addition.length() != 11) {
			System.err.println("String length must be 9");
			return;
		}
		
		// Used to access the bitboard at the right spot while skipping the spaces
		int index = 0;
		
		// Iterate over the string
		for (int i = 0; i < 11; i++) {
			
			// Skip the spaces
			if (i == 3 || i == 7) {
				continue;
			}
			
			if (addition.charAt(i) == 'X') {
				this.set(index, 0);
			} else if (addition.charAt(i) == 'O') {
				this.set(index, 1);
			}
			
			// Increment the index only if a space hasn't been skipped
			index++;
		}
	}
	
	/**
	 * Checks if the current player has won the game.
	 * Updates the game status accordingly.
	 * @return True if the current player has won, false otherwise.
	 */
	public GameStatus checkIfWon() {
		char draw = 0b111111111;
		char occupied = (char) (board[0] | board[1]);
		
		if ((occupied & draw) == draw) {
			gameStatus = GameStatus.DRAW;
			return gameStatus;
		}
		
		
		// All possible winning positions
		char[] positions = new char[]{
				0b111, 0b110001000, 0b1110000, // Horizontal
				0b11000001, 0b100100010, 0b11100, // Vertical
				0b100010001, 0b101000100 // Diagonal
		};
		
		for (char pos : positions) {
			// If the result of a bitwise And is equal to the input, the player has occupied all necessary fields
			if ((board[0] & pos) == pos) {
				gameStatus = GameStatus.ONE;
				return gameStatus;
			} else if ((board[1] & pos) == pos) {
				gameStatus = GameStatus.TWO;
				return gameStatus;
			}
		}
		
		gameStatus = GameStatus.RUNNING;
		
		return gameStatus;
	}
	
	public int partialWinsDifference(GameStatus player) {
		char[] partialWins = new char[]{
				// Horizontal
				0b11, 0b101, 0b110,
				0b110000000, 0b10001000, 0b110000000,
				0b1100000, 0b110000, 0b1010000,
				//Vertical
				0b10000001, 0b1000001, 0b11000000,
				0b100000010, 0b100100000, 0b100010,
				0b1100, 0b11000, 0b10100,
				//Diagonal
				0b100010000, 0b10001, 0b100000001,
				0b101000000, 0b1000001, 0b100000001
		};
		
		int accu = 0;
		
		for (int i = 0; i < partialWins.length; i++) {
			if ((board[0] & partialWins[i]) == partialWins[i]) {
				return accu++;
			} else if ((board[1] & partialWins[i]) == partialWins[i]) {
				return accu--;
			}
		}
		
		return accu;
	}
	
	/**
	 * Rotates the board to the right. <p>
	 * Equivalent to a left rotation of the lower byte.
	 * @param amountOfTurns The amount of turns to rotate the board. 0-3 times
	 * @see java.lang.Integer#rotateLeft(int, int)
	 */
	public void rotateRight(int amountOfTurns) {
		for (int i = 0; i < 2; i++) {
			byte temp = (byte) board[i];
			
			int turns = (amountOfTurns % 4) * 2;
			
			temp = (byte) ((byteToInt(temp) << turns) | ((byteToInt(temp) >>> (8 - turns))));
			
			char middle = (char) (board[i] & 0b100000000);
			
			board[i] = (char) ( middle | (byteToInt(temp)));
		}
	}
	
	/**
	 * Converts an unsigned byte to an integer. <p>
	 * Necessary because Java doesn't support unsigned types. <p>
	 *
	 * @param b The unsigned byte to convert.
	 * @return The integer representation of the byte.
	 * @see <a href="https://stackoverflow.com/a/7401635">Source</a>
	 */
	public int byteToInt(byte b) {
		return b & 0xFF;
	}
	
	/**
	 * Rotates the board to the left. <p>
	 *
	 * Uses a right rotation internally
	 * @param amountOfTurns The amount of turns to rotate the board. 0-3 times
	 */
	public void rotateLeft(int amountOfTurns) {
		this.rotateRight(4-amountOfTurns);
	}
	
	/**
	 * Flips the board horizontally.
	 */
	public void flipHorizontally() {
		for (int i = 0; i < 2; i++) {
			// Extracts the bits that need to be flipped
			byte zero = (byte) ((board[i] & 0b1) << 6);
			byte one = (byte) ((board[i] & 0b10) << 4);
			byte two = (byte) ((board[i] & 0b100) << 2);
			byte middle = (byte) (board[i] & 0b110001000);
			byte six = (byte) ((board[i] & 0b1000000) >> 6);
			byte seven = (byte) ((board[i] & 0b100000) >> 4);
			byte eight = (byte) ((board[i] & 0b10000) >> 2);
			
			board[i] = (char) (zero | one | two | middle | six | seven | eight );
		}
	}
	
	/**
	 * Flips the board vertically.
	 */
	public void flipVertically() {
		this.flipHorizontally();
		this.rotateLeft(2);
	}
	
	/**
	 * Extracts the board for the given player. <p>
	 * <b>For debug purposes and cloning only!</b>
	 * @param player The player to extract the board for.
	 * @return The board for the given player.
	 */
	public char extractChar(int player) {
		return board[player];
	}
	
	public GameStatus getPos(int index) {
		int i = fromHumanToBit(index);
		
		if ((board[0] & (1 << i)) != 0) {
			return GameStatus.ONE;
		} else if ((board[1] & (1 << i)) != 0) {
			return GameStatus.TWO;
		}
		
		return null;
	}
	
	public Bitboard deepClone() {
		return new Bitboard(this);
	}
	
	/**
	 * Translates the bit representation to the human-readable representation
	 * @param i the number to be translated
	 * @return the translation
	 */
	public static int fromBitToHuman(int i) {
		// Translation from bit representation to human-readable
		int[] translation = new int[]{0,1,2,5,8,7,6,3,4};
		return translation[i];
	}
	
	/**
	 * Translates the huma-readable representation to the bit representation
	 * @param i the number to be translated
	 * @return the translation
	 */
	public static int fromHumanToBit(int i) {
		// Translation from human-readable to bit representation
		int[] translation = new int[]{0,1,2,7,8,3,6,5,4};
		return translation[i];
	}
	
	/**
	 * Creates a new Bitboard from a string. <p>
	 * Use capital x for player 1 and capital o for player 2. <p>
	 * Example: "_X_ OXO _X_" -> <p>
	 * _ | X | _ <p>
	 * O | X | O <p>
	 * _ | X | _
	 * @param s The String to be converted.
	 * @return The Bitboard created from the string.
	 */
	public static Bitboard fromString(String s) {
		if (s.length() != 11) {
			System.err.println("String length must be 9");
			return null;
		}
		
		Bitboard bitboard = new Bitboard(0);
		
		// Used to access the bitboard at the right spot while skipping the spaces
		int index = 0;
		
		// Iterate over the string
		for (int i = 0; i < 11; i++) {
			
			// Skip the spaces
			if (i == 3 || i == 7) {
				continue;
			}
			
			if (s.charAt(i) == 'X') {
				bitboard.set(index, 0);
			} else if (s.charAt(i) == 'O') {
				bitboard.set(index, 1);
			}
			
			// Increment the index only if a space hasn't been skipped
			index++;
		}
		
		return bitboard;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < 9; i++) {
			int pos = fromHumanToBit(i);
			
			if ((board[0] & (1 << pos)) != 0) {
				sb.append("X");
			} else if ((board[1] & (1 << pos)) != 0) {
				sb.append("O");
			} else {
				sb.append("_");
			}
			
			if (i % 3 == 2 && i < 8) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	public String lineToString( int line){
		assert line > 0 && line <= 3;
		StringBuilder sb = new StringBuilder();
//		line++;
		int[][] lookup = new int[][]{
				new int[]{0,1,2}, new int[]{3,4,5}, new int[]{6,7,8}
		};
		
		for (int i = 0; i < 3; i++) {
			GameStatus s = getPos(lookup[line][i]);
			if(s == GameStatus.ONE){
				sb.append("X ");
			} else if (s == GameStatus.TWO){
				sb.append("O ");
			} else {
				sb.append("~ "); // •
			}
		}
		
		return sb.toString();
	}
}

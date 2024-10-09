package com.jcvb;

import java.util.ArrayList;
import java.util.List;

public class UltimateBoard {
	public Bitboard[] boardArray;
	private int nextBitBoard;
	private GameStatus currentPlayer;
	private GameStatus winner;
	private GameStatus status;
	
	public UltimateBoard() {
		boardArray = new Bitboard[9];
		for(int i = 0; i < 9; i++) {
				boardArray[i] = new Bitboard(i);
		}
		nextBitBoard = -1;
		currentPlayer = GameStatus.ONE;
	}
	
	private UltimateBoard(Bitboard[] boardArray, int nextBitBoard, GameStatus nextPlayer) {
		this.boardArray = boardArray;
		this.nextBitBoard = nextBitBoard;
		this.currentPlayer = nextPlayer;
	}
	
	public Bitboard getBoard(int index) {
		if(index < 0 || index > 8) {
			System.err.println("Board Index" + index +" out of Bounds");
			return null;
		} else {
			return boardArray[index];
		}
	}
	
	/**
	 * Returns a list of the possible moves for all Boards contained in the Ultimate-Board.
	 * Only returns the possible moves for the next Board, if the next Board is still running.
	 * @return The List of all possible moves
	 */
	public List<Integer> getPossibleMoves() {
		ArrayList<Integer> possibleMoves = new ArrayList<>();
		
		if(nextBitBoard != -1) {
			// BitBoard abrufen und prüfen, ob es noch mögliche Züge gibt
			Bitboard lastBoard = getBoard(nextBitBoard);
			GameStatus lastGameStatus = lastBoard.checkIfWon();
			if (lastGameStatus == GameStatus.RUNNING) {
				lastBoard.getPossibleMoves(possibleMoves);
				return possibleMoves;
			}
		}
		
		// Falls nicht, dann alle Boards durchgehen und prüfen, ob es noch mögliche Züge gibt
		for (int i = 0; i < 9; i++) {
			GameStatus status = boardArray[i].checkIfWon();
			if (status == GameStatus.RUNNING) {
				getBoard(i).getPossibleMoves(possibleMoves);
			}
		}
		return possibleMoves;
	}
	
	/**
	 * Method to make a move on the Ultimate TTT Board
	 * @param index index of the move
	 */
	public void makeMove(int index) {
		int boardIndex = index / 9;
		int posIndex = index % 9;
		nextBitBoard = posIndex;
		
		boardArray[boardIndex].set(posIndex, currentPlayer.playerNumber());
		
		// check if the last move has won the board
		GameStatus gameStatus = boardArray[boardIndex].checkIfWon();
		
		currentPlayer = currentPlayer.next();
	}
	
	/**
	 * Method checks if a player has won the Ultimate TTT Game
	 * @param player player who played last
	 * @return GameStatus.Player, if he has 3 winning boards in a row - null if he hasn't won - GameStatus.DRAW if game is draw
	 */
	public GameStatus checkGameWon(GameStatus player) {
		// horizontal checks
		for (int i = 0; i < 9; i=i+3) {
			if ((boardArray[i].getGameStatus() == player) && (boardArray[i+1].getGameStatus() == player) && (boardArray[i+2].getGameStatus() == player)) {
				return player;
			}
		}
		
		// vertical checks
		int[][] vertical = new int[][]{
				new int[]{0, 3, 6},
				new int[]{1, 4, 7},
				new int[]{2, 5, 8}
		};
		for (int i = 0; i < 3; i++) {
			int[] pos = vertical[i];
			
			if ((boardArray[pos[0]].getGameStatus() == player) && (boardArray[pos[1]].getGameStatus() == player) && (boardArray[pos[2]].getGameStatus() == player)){
				return player;
			}

		}
		// diagonal checks
		if ((boardArray[0].getGameStatus() == player) && (boardArray[4].getGameStatus() == player) && (boardArray[8].getGameStatus() == player)) {
			return player;
		}
		if ((boardArray[2].getGameStatus() == player) && (boardArray[4].getGameStatus() == player) && (boardArray[6].getGameStatus() == player)){
			return player;
		}
		return checkDraw();
	}


	public boolean isGameOver(){
		return checkGameWon(currentPlayer) != GameStatus.RUNNING;
	}

	public GameStatus getWinner() {
		if(checkGameWon(currentPlayer) == currentPlayer){
			winner = currentPlayer;
			return currentPlayer;
		} else if(checkGameWon(currentPlayer.next()) == currentPlayer.next()){
			winner = currentPlayer.next();
			return currentPlayer.next();
		} else if(checkDraw() == GameStatus.DRAW){
			winner = GameStatus.DRAW;
			return GameStatus.DRAW;
		} else {
			winner = GameStatus.RUNNING;
			return GameStatus.RUNNING;
		}
	}

	private GameStatus checkDraw() {
		for (int i = 0; i < 9; i++) {
				if(boardArray[i].getGameStatus() == GameStatus.RUNNING)
					return GameStatus.RUNNING;
			
		}
		return GameStatus.DRAW;
	}
	
	private static final int[][]partialWins = new int[][]{
			//Horizontal
			new int[]{0,1}, new int[]{0,2}, new int[]{1,2},
			new int[]{3,4}, new int[]{3,5}, new int[]{4,5},
			new int[]{6,7}, new int[]{6,8}, new int[]{7,8},
			//Vertical
			new int[]{0,3}, new int[]{0,6}, new int[]{3,6},
			new int[]{1,4}, new int[]{1,7}, new int[]{4,7},
			new int[]{2,5}, new int[]{2,8}, new int[]{5,8},
			// Diagonal
			new int[]{0,4}, new int[]{0,8}, new int[]{4,8},
			new int[]{2,4}, new int[]{2,6}, new int[]{4,6}
	};
	
	/**
	 * Returns the number of partial wins for a player minus the number of partial wins for the opponent
	 * @param player The player for whom the partial wins should be counted
	 * @return The number of partial wins for the player minus the number of partial wins for the opponent
	 */
	public int partialWinsDifference(GameStatus player) {
		int accu = 0;
		
		for (int i = 0; i < partialWins.length; i++) {
			int[] pos = partialWins[i];
			if ((boardArray[pos[0]].getGameStatus() == player) && (boardArray[pos[1]].getGameStatus() == player))
				accu++;
			
			if ((boardArray[pos[0]].getGameStatus() == player.next()) && (boardArray[pos[1]].getGameStatus() == player.next()))
				accu--;
		}
		
		return accu;
	}
	
	public GameStatus getCurrentPlayer() {
		return currentPlayer;
	}
	
	public UltimateBoard deepClone() {
		Bitboard[] copy = new Bitboard[9];
		
		for (int i = 0; i < 9; i++) {
			copy[i] = boardArray[i].deepClone();
		}
		
		return new UltimateBoard(copy, nextBitBoard, currentPlayer);
	}
	
	public int getNextBitBoard() {
		return nextBitBoard;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int boardBase = 0;
		
		// 9 Zeilen
		for(int verticalLine = 0; verticalLine < 9; verticalLine++) {
			
			// mit jeweils 3 Boards
			for(int horizontalBoard = 0; horizontalBoard < 3; horizontalBoard++){
				index = boardBase + horizontalBoard;
				sb.append(getBoard(index).lineToString(verticalLine%3));
//				getBoard(index).getPossibleMoves();
				if(horizontalBoard < 2){
					sb.append("| ");
				} else {
					if(verticalLine != 8){
						sb.append("\n");
					}
				}
			}
			
			if (verticalLine == 2 || verticalLine == 5) {
				sb.append("- - - + - - - + - - -\n");
				boardBase+=3;
			}
		}
		sb.append("\n");
		return sb.toString();
	}
}

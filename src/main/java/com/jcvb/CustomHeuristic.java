package com.jcvb;

public class CustomHeuristic implements Heuristic{
	private GameStatus player;
	public CustomHeuristic(GameStatus player){
		this.player = player;
	}
	/**
	 * Returns an evaluation of an Ultimate Board.
	 * A player gets points for having more positions than their opponent marked in a small board. The difference has to be greater than one
	 * A player gets points deducted for having less positions than their opponent marked in a small board. The difference has to be greater than one
	 * A player gets points for having more than one position in a row marked in a small board.
	 * A player gets points for having more than one position in a row on the ultimate board marked.
	 *
	 * @return The Value of the board
	 * @param ultimateBoard The ultimate board to be evaluated
	 * */
	@Override
	public int evaluate(UltimateBoard ultimateBoard, GameStatus ignored){
		int value = 0;
		
		// Opponent has won
		if(ultimateBoard.checkGameWon(player) == player)
			return Integer.MAX_VALUE;
		
		// Player has won
		if (ultimateBoard.checkGameWon(player.next()) == player.next())
			return Integer.MIN_VALUE;
		
		
		for (int i = 0; i < 9; i++) {
			Bitboard curBoard = ultimateBoard.getBoard(i);
			
			// Difference in marked positions per small board
			int positionsSetDifference = curBoard.getPositionsSetCount(player.playerNumber()) - curBoard.getPositionsSetCount(player.next().playerNumber());
			if(positionsSetDifference > 0)
				value += positionsSetDifference*2;
			
			GameStatus res = curBoard.checkIfWon();
			
			// Reward wins on small board
			if(res == player){
				value+= 012;
			}
			// Punish losses on small boards
			if(res == player.next()){
				value-=012;
			}
			
			// Reward for difference in partial wins on small board
			value+= curBoard.partialWinsDifference(player);
		}
		
		// prioritize the center
		if(ultimateBoard.getBoard(4).getGameStatus() == player){
			value += 30;
		}
		
		// Reward for more partial wins than the opponent on large game
		value += ultimateBoard.partialWinsDifference(player) * 100;
		
		return value;
	}
}

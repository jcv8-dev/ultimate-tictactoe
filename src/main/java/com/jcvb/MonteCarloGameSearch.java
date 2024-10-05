package com.jcvb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonteCarloGameSearch implements Heuristic{
	
	private final int amountOfSimulations;
	
	public MonteCarloGameSearch(int amountOfSimulations) {
		this.amountOfSimulations = amountOfSimulations;
	}
	
	@Override
	public int evaluate(UltimateBoard ultimateBoard, GameStatus player) {
		List<Integer> moves = ultimateBoard.getPossibleMoves();
		Map<Integer, Integer> moveWins = new HashMap<>();
		
		for (int move : moves) {
			
			UltimateBoard copy = ultimateBoard.deepClone();
			copy.makeMove(move);
			
			for (int i = 0; i < amountOfSimulations; i++) {
				GameStatus result = copy.checkGameWon(player);
				if (result == player) {
					moveWins.put(move, moveWins.getOrDefault(move, 0) + 1);
					break;
				} else if (result != GameStatus.RUNNING) {
					break;
				}
				
				List<Integer> possibleMoves = copy.getPossibleMoves();
				
				int nextMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
				
				copy.makeMove(nextMove);
			}
		}
		
		int bestMove = 0;
		int bestWins = 0;
		
		for (Map.Entry<Integer, Integer> entry : moveWins.entrySet()) {
			if (entry.getValue() > bestWins) {
				bestMove = entry.getKey();
				bestWins = entry.getValue();
			}
		}
		
		return bestWins;
	}
}

package com.jcvb;

import java.util.List;

public class RandomPlayer implements Player {
	
	public void play(UltimateBoard ultimateBoard) {
		List<Integer> moves = ultimateBoard.getPossibleMoves();
		
		int move = moves.get((int) (Math.random() * moves.size()));
		
		ultimateBoard.makeMove(move);
	}

	@Override
	public String getName() {
		return "RandomPlayer";
	}

	@Override
	public String getSymbol() {
		return "";
	}
}

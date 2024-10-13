package com.jcvb;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MiniMax implements Player {

	private final int MAX_DEPTH;
	private final Heuristic heuristic;
	private final String name;
	
	public MiniMax(Heuristic heuristic, int depth) {
		this.heuristic = heuristic;
		this.MAX_DEPTH = depth;
		this.name = "MiniMax w/ " + heuristic.getClass().getSimpleName();
	}
	
	public int minimax(UltimateBoard ultimateBoard, int depth, boolean isMaximizing, int alpha, int beta){
		List<Integer> moves = ultimateBoard.getPossibleMoves();
		
		if(moves.isEmpty() || depth == 0){
			return heuristic.evaluate(ultimateBoard, ultimateBoard.getCurrentPlayer());
		}
		
		if(isMaximizing) {
			for (int move : moves) {
				UltimateBoard copy = ultimateBoard.deepClone();
				copy.makeMove(move);
				alpha = Math.max(alpha, minimax(copy, depth - 1, false, alpha, beta));
				
				if (alpha >= beta)
					return alpha;
			}
			return alpha;
		} else {
			for(int move: moves) {
				UltimateBoard copy = ultimateBoard.deepClone();
				copy.makeMove(move);
				beta = Math.min(beta, minimax(copy, depth - 1, true, alpha, beta));
				
				if (alpha >= beta)
					return beta;
			}
			return beta;
		}
	}
	
	public int getBestMove(UltimateBoard ultimateBoard){
		List<Integer> moves =  ultimateBoard.getPossibleMoves();
		int bestMove = 0;
		int bestValue = 0;
		for(int move: moves) {
			UltimateBoard copy = ultimateBoard.deepClone();
			copy.makeMove(move);
			int moveValue = minimax(copy, MAX_DEPTH, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
			if(moveValue > bestValue){
				bestMove = move;
				bestValue = moveValue;
			}
			
		}
		return bestMove;
	}
	
	public int getBestMoveThreaded(UltimateBoard ultimateBoard) {
		List<Integer> moves =  ultimateBoard.getPossibleMoves();
//		for(int i = 0; i < moves.size(); i++){
//			if(moves.size() == 1) {
//				break;
//			}
//			if(Math.random() < 0.2){
//				moves.remove(i);
//				i--;
//			}
//		}
		List<CompletableFuture<Integer>> futures = new ArrayList<>();
		
		for (int move : moves) {
			CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
				UltimateBoard copy = ultimateBoard.deepClone();
				copy.makeMove(move);
				return minimax(copy, MAX_DEPTH, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
			});
			futures.add(future);
		}
		
		int bestMove = 0;
		int bestValue = Integer.MIN_VALUE;
		
		for (int i = 0; i < futures.size(); i++) {
			int moveValue = (int) futures.get(i).join();
			if (moveValue > bestValue) {
				bestMove = moves.get(i);
				bestValue = moveValue;
			}
		}
		
		return bestMove;
	}
	
	public void play(UltimateBoard ultimateBoard) {
		int bestMove = getBestMoveThreaded(ultimateBoard);
		ultimateBoard.makeMove(bestMove);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSymbol() {
		return "";
	}

	public static void main(String[] args) {
		GameStatus result = new Game(new MiniMax(new CustomHeuristic(GameStatus.ONE), 7), new MiniMax(new CustomHeuristic(GameStatus.TWO), 7)).run();
		System.out.println(result);
	}

	@Override
	public String getParam(){
		return "Depth: " + MAX_DEPTH;
	}
}

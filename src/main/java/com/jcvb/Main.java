package com.jcvb;

public class Main {
	public static void main(String[] args) {
		
		int NUM_GAMES = 100;
		// MiniMax, RandomPlayer, Draw
		int[] wins = new int[3];
		int DEPTH = 6;
		
		for (int i = 0; i < NUM_GAMES; i++) {
			if (i % 2 == 0) {
				GameStatus result = new Game(new MiniMax(new CustomHeuristic(GameStatus.ONE), DEPTH), new RandomPlayer()).run();
				wins[result.ordinal()]++;
				printVictoryMessage(result, i, "MiniMax", "RandomPlayer");
			} else {
				GameStatus result = new Game(new RandomPlayer(), new MiniMax(new CustomHeuristic(GameStatus.TWO), DEPTH)).run();
				int ordinal = result.ordinal();
				
				if (ordinal == 0) {
					ordinal = 1;
				} else if (ordinal == 1) {
					ordinal = 0;
				}
				
				wins[ordinal]++;
				printVictoryMessage(result, i, "RandomPlayer", "MiniMax");
			}
		}
		
		System.out.println("MiniMax: " + wins[0]);
		System.out.println("RandomPlayer: " + wins[1]);
		System.out.println("Draw: " + wins[2]);
	}
	
	public static void printVictoryMessage(GameStatus status, int gameNum, String oneName, String twoName) {
		if (status == GameStatus.ONE) {
			System.out.println(gameNum + ": "+ oneName + " wins!\n");
		} else if (status == GameStatus.TWO) {
			System.out.println(gameNum + ": "+ twoName + " wins!\n");
		} else {
			System.out.println(gameNum + ": "+ "It's a draw!\n");
		}
	}
}
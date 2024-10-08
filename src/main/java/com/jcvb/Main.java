package com.jcvb;

public class Main {
	public static void main(String[] args) {
		
		int NUM_GAMES = 100;
		// MiniMax, RandomPlayer, Draw
		int[] wins = new int[3];
		int DEPTH = 8;
		Player monteCarloTreeSearch1 = new MiniMax(new CustomHeuristic(GameStatus.ONE), DEPTH);//new MonteCarloTreeSearch(GameStatus.ONE, new CustomHeuristic(GameStatus.ONE));
		Player monteCarloTreeSearch2 = new MiniMax(new CustomHeuristic(GameStatus.TWO), DEPTH);//new MonteCarloTreeSearch(GameStatus.TWO, new CustomHeuristic(GameStatus.TWO));
		Player randomPlayer = new RandomPlayer();

		for (int i = 0; i < NUM_GAMES; i++) {
			GameStatus result;
			if (i % 2 == 0) {
				result = new Game(randomPlayer, monteCarloTreeSearch2).run();
				if(result.ordinal() == 0){
					wins[1]++;
				} else if(result.ordinal() == 1){
					wins[0]++;
				}
				printVictoryMessage(result, i, "Random", "MonteCarloTreeSearch");
			} else {
				result = new Game(monteCarloTreeSearch1, randomPlayer).run();
				wins[result.ordinal()]++;


				printVictoryMessage(result, i, "MonteCarloTreeSearch", "Random");
			}
//			wins[result.ordinal()]++;
			printStats(wins, monteCarloTreeSearch1, randomPlayer);
		}
		

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

	public static void printStats(int[] wins, Player playerOne, Player playerTwo){
		System.out.println(playerOne.getName() +": " + wins[0]);
		System.out.println(playerTwo.getName() +": " + wins[1]);
		System.out.println("Draw: " + wins[2]);
	}
}
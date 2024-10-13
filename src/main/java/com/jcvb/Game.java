package com.jcvb;

public class Game {
	private Player one;
	private Player two;
	int oneMoves;
	long oneMs;
	int twoMoves;
	long twoMs;
	private UltimateBoard ultimateBoard = new UltimateBoard();
	public Game(Player one, Player two) {
		this.one = one;
		this.two = two;
		this.oneMoves = 0;
		this.twoMoves = 0;
		this.oneMs = 0;
		this.twoMs = 0;
	}
	
	public GameStatus run() {
		GameStatus status = ultimateBoard.getWinner();
		while (status == GameStatus.RUNNING) {
			System.out.println(one.getName()+" is thinking");
			long tempMs = System.currentTimeMillis();
			one.play(ultimateBoard);
			oneMs += System.currentTimeMillis() - tempMs;
			oneMoves++;
			status = ultimateBoard.getWinner();
			if (status != GameStatus.RUNNING) {
				break;
			}
			System.out.println(two.getName()+" is thinking");
			tempMs = System.currentTimeMillis();
			two.play(ultimateBoard);
			twoMs += System.currentTimeMillis() - tempMs;
			twoMoves++;
			status = ultimateBoard.getWinner();
			System.out.println(ultimateBoard);
		}
		return status;
	}

	public Stats getStats(){
		return new Stats(one.getName(), two.getName(), ultimateBoard.getWinner(), oneMoves, twoMoves, one.getParam(), two.getParam(), oneMs/oneMoves, twoMs/twoMoves);
	}


}



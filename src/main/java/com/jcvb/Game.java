package com.jcvb;

public class Game {
	private Player one;
	private Player two;
	private UltimateBoard ultimateBoard = new UltimateBoard();
	public Game(Player one, Player two) {
		this.one = one;
		this.two = two;
	}
	
	public GameStatus run() {
		GameStatus status = ultimateBoard.getWinner();
		while (status == GameStatus.RUNNING) {
			System.out.println(one.getName()+" is thinking");
			one.play(ultimateBoard);
			status = ultimateBoard.getWinner();
//			System.out.println(ultimateBoard);

			if (status != GameStatus.RUNNING) {
				break;
			}
			System.out.println(two.getName()+" is thinking");
			two.play(ultimateBoard);
			status = ultimateBoard.getWinner();
			System.out.println(ultimateBoard);
		}
		return status;
	}
}

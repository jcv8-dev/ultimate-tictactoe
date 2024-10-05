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
		GameStatus status = GameStatus.RUNNING;
		while (status == GameStatus.RUNNING) {
			one.play(ultimateBoard);
			status = ultimateBoard.checkGameWon(GameStatus.ONE);
			//System.out.println(ultimateBoard);
			if (status != GameStatus.RUNNING) {
				break;
			}
			two.play(ultimateBoard);
			status = ultimateBoard.checkGameWon(GameStatus.TWO);
			//System.out.println(ultimateBoard);
		}
		System.out.println(ultimateBoard);
		return status;
	}
}

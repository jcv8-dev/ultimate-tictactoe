package com.jcvb;

import java.util.Scanner;

public class HumanPlayer implements Player {
	@Override
	public void play(UltimateBoard ultimateBoard) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println(ultimateBoard);
		
		int board = ultimateBoard.getNextBitBoard();
		
		if(board == -1) {
			System.out.println("Enter the board you want to make your move on (1-9): ");
			
			board = scanner.nextInt() -1;
		} else {
			GameStatus status = ultimateBoard.getBoard(board).checkIfWon();
			
			if (status != GameStatus.RUNNING) {
				System.out.println("Enter the board you want to make your move on (1-9): ");
				
				board = scanner.nextInt() -1;
			} else {
				System.out.println("You have to make your move on board " + (board + 1));
			}
		}
		
		System.out.println("Enter the position you want to make your move on (1-9): ");
		
		int position = scanner.nextInt() - 1;
		
		ultimateBoard.makeMove(board * 9 + position);
	}

	public String getName(){
		return "Human";
	}

	@Override
	public String getSymbol() {
		return "";
	}

	public static void main(String[] args) {
		GameStatus result = new Game(new HumanPlayer(), new MiniMax(new CustomHeuristic(GameStatus.TWO), 7)).run();
		System.out.println(result);
	}
	@Override
	public String getParam(){
		return "Mensch";
	}
}

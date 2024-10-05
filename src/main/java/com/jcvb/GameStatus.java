package com.jcvb;

public enum GameStatus {
	ONE,
	TWO,
	DRAW,
	RUNNING;
	
	public int playerNumber() {
		if (this == ONE) {
			return 0;
		} else if (this == TWO) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public GameStatus next() {
		if (this == ONE) {
			return TWO;
		} else if (this == TWO) {
			return ONE;
		} else {
			return RUNNING;
		}
	}
}

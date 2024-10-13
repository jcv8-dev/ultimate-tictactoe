package com.jcvb;

public record Stats(String player1_name, String player2_name, GameStatus winner, int player1_moves, int player2_moves, String player1_param, String player2_param, long player1_avg_ms, long player2_avg_ms) {}

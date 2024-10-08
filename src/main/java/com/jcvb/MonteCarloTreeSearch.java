package com.jcvb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearch implements Player {

    private final GameStatus player;
    private final int simulations = 100000;  // Number of MCTS simulations
    private final double explorationConstant = Math.sqrt(2);  // UCT exploration constant
    private final Heuristic heuristic;  // Reference to the Heuristic interface implementation

    public MonteCarloTreeSearch(GameStatus player, Heuristic heuristic) {
        this.player = player;
        this.heuristic = heuristic;
    }

    @Override
    public void play(UltimateBoard ultimateBoard) {
        int bestMove = findBestMove(ultimateBoard);
        if (bestMove != -1) {
            ultimateBoard.makeMove(bestMove);
        }
    }

    private int findBestMove(UltimateBoard ultimateBoard) {
        Node root = new Node(ultimateBoard.deepClone(), null, -1);
        for (int i = 0; i < simulations; i++) {
            Node selectedNode = selectNode(root);
            GameStatus winner = selectedNode.ultimateBoard.checkGameWon(selectedNode.playerToMove);

            // If the game isn't finished, we expand the node and simulate using heuristics
            if (winner == GameStatus.RUNNING) {
                expandNode(selectedNode);
                GameStatus rolloutResult = simulateHeuristicPlay(selectedNode);
                backpropagate(selectedNode, rolloutResult);
            } else {
                // If game is finished, propagate the result immediately
                backpropagate(selectedNode, winner);
            }
        }

        // Choose the best move from the root node based on visit count or win rate
        Node bestChild = root.getBestChild();
        return (bestChild != null) ? bestChild.move : -1;
    }

    private Node selectNode(Node node) {
        while (!node.isLeaf()) {
            node = node.getBestUCTChild(explorationConstant);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<Integer> possibleMoves = node.ultimateBoard.getPossibleMoves();
        for (int move : possibleMoves) {
            UltimateBoard newBoard = node.ultimateBoard.deepClone();
            newBoard.makeMove(move);  // Applying the move on the cloned board
            node.children.add(new Node(newBoard, node, move));
        }
    }

    /**
     * Heuristic-based simulation (rollout phase).
     */
    private GameStatus simulateHeuristicPlay(Node node) {
        UltimateBoard simulatedBoard = node.ultimateBoard.deepClone();
        GameStatus currentPlayer = node.playerToMove;

        while (simulatedBoard.checkGameWon(currentPlayer) == GameStatus.RUNNING) {
            List<Integer> possibleMoves = simulatedBoard.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                break;
            }

            // Choose the best move based on heuristic evaluation
            int bestMove = chooseHeuristicMove(simulatedBoard, currentPlayer);
            simulatedBoard.makeMove(bestMove);  // Use makeMove instead of playMove

            // Switch to the next player
            currentPlayer = (currentPlayer == GameStatus.ONE) ? GameStatus.TWO : GameStatus.ONE;
        }

        return simulatedBoard.checkGameWon(currentPlayer);
    }

    /**
     * Selects the best move based on the heuristic.
     */
    private int chooseHeuristicMove(UltimateBoard board, GameStatus currentPlayer) {
        List<Integer> possibleMoves = board.getPossibleMoves();
        int bestMove = possibleMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (int move : possibleMoves) {
            UltimateBoard clone = board.deepClone();
            clone.makeMove(move);  // Use makeMove instead of playMove

            // Evaluate the board from the perspective of the current player or opponent
            int score = heuristic.evaluate(clone, currentPlayer);

            // If evaluating the opponent's move, flip the score (opponent's gain is current player's loss)
            if (currentPlayer != this.player) {
                score = -score;
            }

            // Choose the move with the highest heuristic score
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;  // Return the best move according to the heuristic
    }

    private void backpropagate(Node node, GameStatus result) {
        while (node != null) {
            node.visits++;
            if (result == node.playerToMove) {
                node.wins++;
            }
            node = node.parent;
        }
    }

    @Override
    public String getName() {
        return "MonteCarloTreeSearch (Heuristic Based)";
    }

    @Override
    public String getSymbol() {
        if(player == GameStatus.ONE) return "X";
        if(player == GameStatus.TWO) return "O";
        return null;
    }

    // Inner class for tree node representation
    private class Node {
        UltimateBoard ultimateBoard;
        Node parent;
        List<Node> children;
        int move;  // Integer representing the move index
        GameStatus playerToMove;
        int wins;
        int visits;

        public Node(UltimateBoard ultimateBoard, Node parent, int move) {
            this.ultimateBoard = ultimateBoard;
            this.parent = parent;
            this.move = move;
            this.children = new ArrayList<>();
            this.playerToMove = (parent == null) ? player : (parent.playerToMove == GameStatus.ONE ? GameStatus.TWO : GameStatus.ONE);
            this.wins = 0;
            this.visits = 0;
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public Node getBestChild() {
            return children.stream().max((n1, n2) -> Integer.compare(n1.visits, n2.visits)).orElse(null);
        }

        public Node getBestUCTChild(double explorationConstant) {
            return children.stream().max((n1, n2) -> Double.compare(n1.getUCTValue(explorationConstant), n2.getUCTValue(explorationConstant))).orElse(null);
        }

        public double getUCTValue(double explorationConstant) {
            if (visits == 0) return Double.MAX_VALUE;
            double winRate = (double) wins / visits;
            double explorationTerm = explorationConstant * Math.sqrt(Math.log(parent.visits) / visits);
            return winRate + explorationTerm;
        }
    }
}

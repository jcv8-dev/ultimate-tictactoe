package com.jcvb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearch implements Player {

    private final GameStatus player;
    private final int simulations = 300000;  // Number of MCTS simulations
    private final double explorationConstant = Math.sqrt(2);  // UCT exploration constant
    private final int msPerMove = 500;

    public MonteCarloTreeSearch(GameStatus player) {
        this.player = player;
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
        long start = System.currentTimeMillis();
        int iteration_count = 0;
        while(System.currentTimeMillis() - start < msPerMove) {
            iteration_count++;
            Node selectedNode = selectNode(root);
            GameStatus winner = selectedNode.ultimateBoard.getWinner();

            // If the game isn't finished, expand and simulate
            if (winner == GameStatus.RUNNING) {
                expandNode(selectedNode);
                GameStatus rolloutResult = simulateRandomPlay(selectedNode);
                backpropagate(selectedNode, rolloutResult);
            } else {
                // Game already finished, propagate result immediately
                backpropagate(selectedNode, winner);
            }
        }
        System.out.println("MCTS went though " + iteration_count + " iterations");
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
     * Random simulation (rollout phase).
     */
    private GameStatus simulateRandomPlay(Node node) {
        UltimateBoard simulatedBoard = node.ultimateBoard.deepClone();
        GameStatus currentPlayer = node.playerToMove;
        Random random = new Random();

        // Play randomly until the game is won or a draw
        while (!simulatedBoard.isGameOver()) {
            List<Integer> possibleMoves = simulatedBoard.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                break;
            }

            // Randomly select a move
            int randomMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
            simulatedBoard.makeMove(randomMove);

            // Switch to the next player
            currentPlayer = currentPlayer.next();
        }

        return simulatedBoard.checkGameWon(currentPlayer);
    }

    private void backpropagate(Node node, GameStatus result) {
        while (node != null) {
            node.visits++;
            if (result == node.playerToMove.next()) {
                node.wins++;  // Reward if it's the winning player
            } else {
                node.wins--;  // Penalize if it's the loosing player or if it's a draw
            }
            node = node.parent;
        }
    }

    @Override
    public String getName() {
        return "MonteCarloTreeSearch (Random Simulation)";
    }

    @Override
    public String getSymbol() {
        if(player == GameStatus.ONE) return "X";
        if(player == GameStatus.TWO) return "O";
        return null;
    }

    @Override
    public String getParam() {
        return "Ms/Move: " + msPerMove;
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
            return children.stream().max(Comparator.comparingInt(n -> n.visits)).orElse(null);
        }

        public Node getBestUCTChild(double explorationConstant) {
            return children.stream().max(Comparator.comparingDouble(n -> n.getUCTValue(explorationConstant))).orElse(null);
        }

        public double getUCTValue(double explorationConstant) {
            if (visits == 0) return Double.MAX_VALUE;
            double winRate = (double) wins / visits;
            double explorationTerm = explorationConstant * Math.sqrt(Math.log(parent.visits) / visits);
            return winRate + explorationTerm;
        }
    }

    public static void main(String[] args) {
        GameStatus result = new Game(new MonteCarloTreeSearch(GameStatus.ONE), new HumanPlayer()).run();
        System.out.println(result);
    }

}

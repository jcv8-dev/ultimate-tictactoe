package com.jcvb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class MonteCarloTreeSearchMultithreaded implements Player {

    private final GameStatus player;
    private final int simulations = 50000;  // Number of MCTS simulations
    private final double explorationConstant = Math.sqrt(2);  // UCT exploration constant
    private final ExecutorService executorService;

    public MonteCarloTreeSearchMultithreaded(GameStatus player) {
        this.player = player;
        // Create a fixed thread pool based on the number of available processors
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
        List<Callable<Node>> tasks = new ArrayList<>();

        for (int move : possibleMoves) {
            tasks.add(() -> {
                UltimateBoard newBoard = node.ultimateBoard.deepClone();
                newBoard.makeMove(move);  // Applying the move on the cloned board
                return new Node(newBoard, node, move);
            });
        }

        try {
            // Submit the tasks and get the results (child nodes)
            List<Future<Node>> futures = executorService.invokeAll(tasks);
            for (Future<Node> future : futures) {
                Node childNode = future.get();
                node.children.add(childNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        while (simulatedBoard.checkGameWon(currentPlayer) == GameStatus.RUNNING) {
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
            if (result == node.playerToMove) {
                node.wins++;
            }
            node = node.parent;
        }
    }

    @Override
    public String getName() {
        return "MonteCarloTreeSearch (Multithreaded Expansion)";
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

    public void shutdown() {
        executorService.shutdown();
    }
}

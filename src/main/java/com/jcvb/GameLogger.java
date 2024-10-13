package com.jcvb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameLogger {
    private static final String BASE_FILENAME = "game_results_";
    private static final String FILE_EXTENSION = ".csv";
    private static int currentFileNumber = 0;

    static {
        initializeCurrentFileNumber();
    }

    private static void initializeCurrentFileNumber() {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.startsWith(BASE_FILENAME) && name.endsWith(FILE_EXTENSION));

        if (files != null && files.length > 0) {
            Pattern pattern = Pattern.compile(BASE_FILENAME + "(\\d+)" + FILE_EXTENSION);

            currentFileNumber = Arrays.stream(files)
                    .map(File::getName)
                    .map(pattern::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> Integer.parseInt(matcher.group(1)))
                    .max(Comparator.naturalOrder())
                    .orElse(0);
        }
    }

    public static void createNewLogFile() {
        currentFileNumber++;
        String filename = getCurrentFilename();
        try (FileWriter writer = new FileWriter(filename)) {
            writeHeader(writer);
            System.out.printf("Writing Log to %s\n", filename);
        } catch (IOException e) {
            System.err.println("Error creating new log file: " + e.getMessage());
        }
    }

    public static void logGameResult(int gameNumber, Stats stats) {
        String filename = getCurrentFilename();

        try (FileWriter writer = new FileWriter(filename, true)) {
            String resultString = (stats.winner() == GameStatus.ONE) ? stats.player1_name() :
                    (stats.winner() == GameStatus.TWO) ? stats.player2_name() : "Draw";

            String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    gameNumber, stats.player1_name(), stats.player2_name(),
                    stats.player1_moves(), stats.player2_moves(),
                    stats.player1_param(), stats.player2_param(),
                    stats.player1_avg_ms(), stats.player2_avg_ms(),
                    resultString, getCurrentTimestamp());

            writer.write(line);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    private static String getCurrentFilename() {
        return BASE_FILENAME + currentFileNumber + FILE_EXTENSION;
    }

    private static void writeHeader(FileWriter writer) throws IOException {
        writer.write("Game Number,Player 1 Name,Player 2 Name,Player 1 Moves, Player 2 Moves, PlPlayer 1 Parameter,Player 2 Parameter,Player 1 Avg Time,Player 2 Avg Time,Winner,Timestamp\n");
    }

    private static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}

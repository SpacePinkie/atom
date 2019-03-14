package ru.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.slf4j.LoggerFactory;

public class Game {

    private final Random myRandom;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Game.class);
    private String fileName = "dictionary.txt";
    private ArrayList<String> dict;
    private Scanner in = new Scanner(System.in);
    private Player mainPlayer;
    private int rangeRandom = 0;

    public Game() {
        log.info("Class: Game is starting");
        myRandom = new Random();
        mainPlayer = new Player();
        dict = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                dict.add(currentLine);
            }
        } catch (IOException e) {
            log.warn("Class: Game error in reading file of dict");
            e.printStackTrace();
        }
        rangeRandom = dict.size() + 1;
        log.info("Class: Game started");
    }

    public void startGame() {
        int indexOfTry = 0;
        int cows = 0;
        int bulls = 0;
        log.info("Class: Game, method: startGame is starting");
        System.out.println("Hello! Who are you?");
        String playerName = in.nextLine();
        mainPlayer.setName(playerName);
        System.out.println("Hi," + playerName + " Do you want to play?");
        System.out.println("y - Yes, n -No");
        String c = in.nextLine();
        if (c.equals("y"))
            while (true) {
                log.info("Class: Game main loop is active");
                String word = readNewWord();
                System.out.println("What is word?");
                while (indexOfTry != 9) {
                    String answer = in.nextLine();
                    cows = checkCows(answer, word);
                    bulls = checkBulls(answer, word);
                    System.out.println("cows is " + cows);
                    System.out.println("bulls is " + bulls);
                    if (bulls == word.length()) {
                        System.out.println("Yes, you win!");
                        mainPlayer.upScore(10);
                        System.out.println("You have " + mainPlayer.getScore() + " points");
                        break;
                    }
                    indexOfTry++;
                }
                if (indexOfTry == 9) {
                    System.out.println("Sorry, but you lost. True word is" + word);
                    mainPlayer.upScore(-5);
                    System.out.println("You have " + mainPlayer.getScore() + "points");
                }
                indexOfTry = 0;
                System.out.println("Do you want to play again?");
                System.out.println("y - Yes, n -No");
                String nswr = in.nextLine();
                if (nswr.equals("n"))
                    break;
            }
        log.info("Class: Game main loop is disable");
        System.out.println("GG. Goodbye! And see you!");
        in.close();
    }

    private String readNewWord() {
        int index = myRandom.nextInt(rangeRandom);
        return dict.get(index);
    }

    public static int checkBulls(String word, String trueWord) {
        int count = 0;
        for (int i = 0; i < trueWord.length(); i++) {
            if (i < word.length()) {
                if (trueWord.charAt(i) == word.charAt(i))
                    count++;
            } else
                break;
        }
        return count;
    }

    public static int checkCows(String word, String trueWord) {
        int count = 0;
        StringBuffer stringBuffer;
        String deltaTrueWord = trueWord;
        for (int i = 0; i < word.length(); i++) {
            if (deltaTrueWord.length() != 0) {
                for (int j = 0; j < deltaTrueWord.length(); j++) {
                    if (deltaTrueWord.charAt(j) == word.charAt(i)) {
                        count++;
                        stringBuffer = new StringBuffer(deltaTrueWord);
                        stringBuffer.delete(j, j + 1);
                        deltaTrueWord = stringBuffer.toString();
                        break;
                    }
                }
            }
        }
        return count;
    }
}

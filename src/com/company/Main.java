package com.company;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {

    private static final String FILENAME = "\\src\\com\\company\\cards";
    private static ArrayList<player> players = new ArrayList<>(); //holds each instance of the players class
    private static  ArrayList<card> deck = new ArrayList<>(); //holds all the cards from the file, allowing them to be shuffled every time
    private static int numPlayers = 0;
    private static String[] attributeNames;
    private static int startingPlayer;
    private static int currentAttribute;

    public static void main(String[] args) {
        readFileData();
        getNumPlaying();
        getPlayerNames(); //these methods set static global variables and initialise the players ArrayList
        newGame();
        shuffleCards();
        dealCards();
        while (players.size() != 1){
            getAttributeChoice(players.get(startingPlayer));
        }
    }

    private static void readFileData() {
        BufferedReader bufferedReader;
        FileReader fileReader;
        try {
            fileReader = new FileReader(System.getProperty("user.dir") + FILENAME);
            bufferedReader = new BufferedReader(fileReader);
            attributeNames = removeSpaces(bufferedReader.readLine());
            String currentLine;
            int counter = 0;
            while((currentLine = bufferedReader.readLine()) != null) {
                deck.add(new card());
                deck.get(counter).setAttributes(removeSpaces(currentLine));
                counter++;
            }
            bufferedReader.close();
            fileReader.close();
            shuffleCards();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //reads data from the card file
    private static String[] removeSpaces(String line) {
        String[] out = line.split(" ");
        for (int i = 0; i < line.length(); i++) {
            out[i] = out[i%out.length].replaceAll("_", " ");
        }
        return out;
    }
    //a method for removing the spaces and underscores used to structure the data in the file
    private static void getNumPlaying(){ //this method gets the number of playing and validates the input
        String playerNumString = JOptionPane.showInputDialog("how many people are playing?");
        boolean validEntry = false;
        do {
            try {
                if (playerNumString == null) {
                    System.exit(0);
                }
                numPlayers = Integer.parseInt(playerNumString);
                validEntry = true;
            } catch (NumberFormatException e) {
                playerNumString = JOptionPane.showInputDialog("please enter a number\nhow many people are playing?");
            }
        } while (!validEntry);
    }
    //this method validates the user entered value for the number of players playing the game
    private static void getPlayerNames() { //this method creates all of the players and uses a constructor to assign their names
        for (int i = 0; i < numPlayers; i++) {
            String name = JOptionPane.showInputDialog("please enter the name of player " + i + ":");
            players.add(new player(name));
        }
    }
    //this method gets the names of each player and initialises the instances of the player class used to represent them
    private static void newGame() {
        startingPlayer = (int) (Math.random() * numPlayers);
        for (player p:players){
            p.newGame();
        }
    }
    //wipes all of the players and assigns a new starting player
    private static void shuffleCards() {
        for (int j = 0; j < 50; j++) { //this shuffles the deck by randomly removing cards and placing them in a new deck
            ArrayList<card> shuffledDeck = new ArrayList<>(); //and it does this 50 times to shuffle it a lot
            while (deck.size() > 0) {
                int index = (int) (Math.random() * deck.size());
                shuffledDeck.add(deck.remove(index));
            }
            deck = shuffledDeck;
        }
    }
    //shuffles the cards
    private static void dealCards() { //this method deals the cards
        for (int i = 0; i < deck.size(); i++) {
            players.get(i%numPlayers).addCard(deck.remove(0));
        }
    }
    //deals the cards
    private static void getAttributeChoice(player p){
        StringBuilder message = new StringBuilder();
        message.append(p.getName());
        message.append(", your current card is:\n");
        for (int i = 0; i < p.getCurrentCard().getAttributes().length; i++) {
            message.append(attributeNames[i]);
            message.append(": ");
            message.append(p.getCurrentCard().getAttributes()[i]);
            message.append("\n");
        }
    }
    //gives the current player the option to choose an attribute from their card
    private static void endRound(player p){
        deck = p.getCards();
    }
    //ends the round by retrieving all of the cards from the winning player's hand.
}

class player {
    private ArrayList<card> cards = new ArrayList<>();
    private String name;

    player(String name){
        this.name = name;
    }
    public void newGame(){
        this.cards.clear();
    }
    public void addCard(card card) {
        this.cards.add(card);
    }
    public String getName() {
        return name;
    }
    public void addCards(ArrayList<card> newCards){
        this.cards.addAll(newCards.stream().collect(Collectors.toList()));
    }
    public card getCurrentCard(){
        return cards.get(0);
    }
    public ArrayList<card> getCards() {
        return cards;
    }
}

class card {
    private String[] attributes;

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }
    public String getCardName(){
        return this.attributes[0];
    }
    public String[] getAttributes() {
        return attributes;
    }
}

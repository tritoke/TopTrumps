package com.company;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class Main {

    private static final String FILENAME = "\\src\\com\\company\\cards.txt";
    //C:\Users\Admin\Documents\GitHub\TopTrumps\src\com\company\cards.txt
    private static ArrayList<player> players = new ArrayList<>(); //holds each instance of the players class
    private static  ArrayList<card> deck = new ArrayList<>(); //holds all the cards from the file, allowing them to be shuffled every time
    private static int numPlayers = 0;
    private static String[] attributeNames;
    private static int currentPlayer;
    private static int currentAttribute;
    private static String[] messageOptions;

    public static void main(String[] args) {
        do {
            readFileData();
            getNumPlaying();
            getPlayerNames();
            //these methods set static global variables and initialise the players ArrayList
            newGame();
            shuffleCards();
            dealCards();
            //these initialise everything needed for the game i.e. dealing cards etc
            while (players.size() != 1) {
                getAttributeChoice(players.get(currentPlayer));

                checkLosses();
            }
            endRound(players.remove(0));
        } while (quit());
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
                String[] splitLine = removeSpaces(currentLine);
                String cardName = splitLine[0];
                ArrayList<Integer> attributeValues = new ArrayList<>();
                for (int i = 1; i < splitLine.length; i++) {
                    attributeValues.add(Integer.parseInt(splitLine[i]));
                }
                deck.add(new card(cardName, attributeValues));
                counter++;
            }
            bufferedReader.close();
            fileReader.close();
            shuffleCards();

            messageOptions = new String[attributeNames.length - 1];
            for (int i = 1; i < attributeNames.length; i++) {
                messageOptions[i-1] = attributeNames[i];
            }   //this for loop adds all of the names of the attributeValues apart from name to a new array
            //this is done so that the user cannot select name as their attribute choice

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //reads data from the card file
    private static String[] removeSpaces(String line) {
        String[] out = line.split(" ");
        for (int i = 0; i < out.length; i++) {
            out[i] = out[i%out.length].replaceAll("_", " ");
        }
        /*System.out.println(line + "\n");
        for (String s:out){
            System.out.println(s);
        }*/
        //remove this comment block to print what it reads from the file
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
        currentPlayer = (int) (Math.random() * numPlayers); //this random function determines the starting player
        for (int i = 0; i < numPlayers; i++) {
            String name = JOptionPane.showInputDialog("please enter the name of player " + (i+1) + ":");
            if (name == null){
                System.exit(0);
            }
            if (i == currentPlayer) {
                players.add(new player(name, true));
            } else {
                players.add(new player(name, false));
            }
        }
    }
    //this method gets the names of each player and initialises the instances of the player class used to represent them
    private static void newGame() {
        players.forEach(player::newGame);
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
    private static void getAttributeChoice( player p){
        StringBuilder message = new StringBuilder();
        message.append(p.getName());
        message.append(", your current card:\n");
        for (int i = 0; i < p.getCurrentCard().getAttributeValues().size(); i++) {
            message.append(attributeNames[i]);
            message.append(": ");
            message.append(p.getCurrentCard().getAttributeValues().get(i));
            message.append("\n");
        }
        message.append("which attribute do you want to choose?");
        //these lines create the message ^^

        String reply = (String) JOptionPane.showInputDialog(
                null,
                message.toString(),
                "value selector",
                JOptionPane.PLAIN_MESSAGE,
                null,
                messageOptions,
                messageOptions[0]);
        if (reply == null) {
            System.exit(0);
        }
        System.out.println(reply);
        for (int i = 0; i < messageOptions.length; i++) {
            if (reply.equals(messageOptions[i])){
                currentAttribute = i;
            }
        }
    }
    //gives the current player the option to choose an attribute from their card
    private static void compareAttributes(){
        int winningPlayerIndex = 0;
        //int maxAttributeValue = Integer.parseInt(players.get(currentPlayer).getCurrentCard().getAttributeValues()[currentAttribute]);
        //this line looks complicated but actually just gets the value of the selected attribute
    }
    //this method goes through all the cards that players
    private static void checkLosses(){
        for (player p:players) {
            if (p.getCards().size() == 0){
                StringBuilder loseMessage = new StringBuilder();
                loseMessage.append(p.getName());
                loseMessage.append(" unfortunately you have lost");
                JOptionPane.showConfirmDialog(
                        null,
                                        loseMessage.toString()
                );
                players.remove(p);
            }
        }
        //TODO create a scoreboard thing
    }
    //this method checks if a player has no cards left, if they do they are removed, it also shows a scoreboard
    private static void endRound(player p){
        deck = p.getCards();
    }
    //ends the round by retrieving all of the cards from the winning player's hand.
    private static boolean quit(){
        int choice = JOptionPane.showConfirmDialog(
                null,
                "do you want to play another round",
                "Quit?",
                JOptionPane.YES_NO_OPTION);
        System.out.println(choice);
        return choice == JOptionPane.YES_OPTION;
    }
    //asks returns a boolean value of whether the user wants to play another round.
}

class player {
    private ArrayList<card> cards = new ArrayList<>();
    private String name;
    private boolean currentPlayer;

    player(String name, boolean currentPlayer){
        this.name = name;
        this.currentPlayer = currentPlayer;
    }

    public void newGame(){
        this.cards.clear();
    }
    public boolean isCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(boolean currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void addCard(card card) {
        this.cards.add(card);
    }
    public String getName() {
        return name;
    }
    public void addCards(ArrayList<card> newCards){
        this.cards.addAll(new ArrayList<>(newCards));
    }
    public card getCurrentCard(){
        return cards.remove(0);
    }
    public ArrayList<card> getCards() {
        return cards;
    }
}

class card {
    private String cardName;
    private ArrayList<Integer> attributeValues;

    card(String cardName, ArrayList<Integer> attributeValues){
        this.cardName = cardName;
        this.attributeValues = attributeValues;
    }

    public String getCardName(){
        return this.cardName;
    }
    public ArrayList<Integer> getAttributeValues() {
        return attributeValues;
    }
}


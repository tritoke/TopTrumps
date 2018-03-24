package com.company;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class Main {

    private static final String FILENAME = "\\src\\com\\company\\cards.txt";
    //C:\Users\Admin\Documents\GitHub\TopTrumps\src\com\company\cards.txt.txt
    private static ArrayList<player> players = new ArrayList<>(); //holds each instance of the players class
    private static  ArrayList<card> deck = new ArrayList<>(); //holds all the cards.txt from the file, allowing them to be shuffled every time
    private static int numPlayers = 0;
    private static String[] attributeNames;
    private static int currentPlayer;
    private static int currentAttribute;
    private static String[] messageOptions;
    private static ArrayList<card> cardPile;

    public static void main(String[] args) {
        do {
            readFileData();
            getNumPlaying();
            getPlayerNames();
            //these methods set static global variables and initialise the players ArrayList
            newGame();
            shuffleCards();
            dealCards();
            //these initialise everything needed for the game i.e. dealing cards.txt etc
            while (players.size() != 1) {
                cardPile = new ArrayList<>();
                //cardPile stores all the cards which players play during the round
                getAttributeChoice(players.get(currentPlayer));
                drawResolver(compareAttributes(players));
                checkLosses();
            }
            endRound(players.remove(0));
        } while (quit("do you want to play another round"));
    }

    private static void readFileData() {
        BufferedReader bufferedReader;
        FileReader fileReader;
        try {
            fileReader = new FileReader(System.getProperty("user.dir") + FILENAME);
            bufferedReader = new BufferedReader(fileReader);
            attributeNames = removeSpaces(bufferedReader.readLine());
            for(String s:attributeNames){
                System.out.println(s);
            }
            String currentLine;
            while((currentLine = bufferedReader.readLine()) != null) {
                String[] splitLine = removeSpaces(currentLine);
                String cardName = splitLine[0];
                ArrayList<Integer> attributeValues = new ArrayList<>();
                for (int i = 1; i < splitLine.length; i++) {
                    attributeValues.add(Integer.parseInt(splitLine[i]));
                }
                deck.add(new card(cardName, attributeValues));
            }
            bufferedReader.close();
            fileReader.close();
            shuffleCards();

            messageOptions = new String[attributeNames.length - 1];
            System.arraycopy(attributeNames, 1, messageOptions, 0, attributeNames.length - 1);
            //this is done so that the user cannot select name as their attribute choice, because it removes the attribute

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
        return out;
    }
    //a method for removing the spaces and underscores used to structure the data in the file
    private static void getNumPlaying(){ //this method gets the number of playing and validates the input
        String playerNumString = JOptionPane.showInputDialog("how many people are playing?");
        boolean validEntry = false;
        do {
            try {
                if (playerNumString == null) {
                    if(quit("are you sure you want to quit?")){
                        System.exit(0);
                    }
                }
                assert playerNumString != null;
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
            while(true) {
                String name = JOptionPane.showInputDialog("please enter the name of player " + (i + 1) + ":");
                if (name == null) {
                    if (quit("are you sure you want to quit?")) {
                        System.exit(0);
                    }
                } else {
                    players.add(new player(name));
                    break;
                }
            }
        }
    }
    //this method gets the names of each player and initialises the instances of the player class used to represent them
    private static void newGame() {
        players.forEach(player::newGame);
    }
    //wipes all of the players
    private static void shuffleCards() {
        for (int j = 0; j < 50; j++) { //this shuffles the deck by randomly removing cards.txt and placing them in a new deck
            ArrayList<card> shuffledDeck = new ArrayList<>(); //and it does this 50 times to shuffle it a lot
            while (deck.size() > 0) {
                int index = (int) (Math.random() * deck.size());
                shuffledDeck.add(deck.remove(index));
            }
            deck = shuffledDeck;
        }
    }
    //shuffles the cards.txt
    private static void dealCards() { //this method deals the cards.txt
        System.out.println(deck.size());
        for (int i = 0; i < deck.size(); i++) {
            players.get(i%numPlayers).addCard(deck.remove(0));
        }

    }
    //deals the cards.txt
    private static void getAttributeChoice(player p){
        card currentCard = p.getCurrentCard();
        StringBuilder message = new StringBuilder();
        message.append(p.getName());
        message.append(", your current card:\n");
        for (int i = 0; i < currentCard.getAttributeValues().size(); i++) {
            message.append(attributeNames[i]);
            message.append(": ");
            message.append(currentCard.getAttributeValues().get(i));
            message.append("\n");
        }
        message.append("which attribute do you want to choose?");
        //these lines create the message ^^
        while(true) {
            String reply = (String) JOptionPane.showInputDialog(
                    null,
                    message.toString(),
                    "value selector",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    messageOptions,
                    messageOptions[0]);
            //this reply string contains the user's response for which attribute of the card they want to play
            if (reply == null) {
                if (quit("are you sure you want to quit?")) {
                    System.exit(0);
                }
            } else {
                //this checks if the user closed the window and exits the program if the user meant to.
                for (int i = 0; i < messageOptions.length; i++) {
                    if (reply.equals(messageOptions[i])) {
                        currentAttribute = i;
                    }
                }
                break;
            }
        }
    }
    //gives the current player the option to choose an attribute from their card
    private static ArrayList<player> compareAttributes(ArrayList<player> playerIn) {
        ArrayList<player> winningPlayers = null;
        for (player p : playerIn) {
            cardPile.add(p.getCurrentCard());
        }
        int maxAttributeValue = 0;
        currentPlayer = 0;
        for (int i = 0; i < playerIn.size(); i++) {
            winningPlayers = new ArrayList<>();
            int currentCardValue = cardPile.get(i).getAttributeValues().get(currentAttribute);
            winningPlayers.add(playerIn.get(i));
            if (currentCardValue > maxAttributeValue) {
                maxAttributeValue = currentCardValue;
                currentPlayer = i;
            } else if (currentCardValue == maxAttributeValue) {
                winningPlayers.add(playerIn.get(i));
            }
        }
        return winningPlayers;
    }
    //this method takes in some players and finds the ones with winning values
    private static void drawResolver(ArrayList<player> winningPlayers) {
        for (player p : winningPlayers) {
            if (p.getCards().size() == 0) {
                winningPlayers.remove(p);
            }
        }
        //this for loop goes through the players and removes all the ones who have no cards
        if (winningPlayers.size() > 1) {
            StringBuilder message = new StringBuilder("there has been a draw between ");
            message.append(winningPlayers.size());
            message.append(" players:\n");
            for (player p : winningPlayers) {
                message.append(p.getName());
                message.append("\n");
            }
            currentPlayer = (int) (Math.random() * winningPlayers.size());
            message.append("\n");
            message.append(winningPlayers.get(currentPlayer).getName());
            message.append(" has been randomly chosen to choose the next attribute");
            JOptionPane.showConfirmDialog(
                    null,
                    message.toString()
            );
            getAttributeChoice(winningPlayers.get(currentPlayer));
            ArrayList<player> remainingPlayers = compareAttributes(winningPlayers);
            if (remainingPlayers.size() != 1) {
                drawResolver(remainingPlayers);
            } else {
                remainingPlayers.get(0).addCards(cardPile);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "everyone who drew has run out of cards,\nso those cards are discarded and a new\n player has been chosen at random.");
            deck.addAll(cardPile);
        }
    }
    //this method resolves draws by recursively calling itself until there one player remains
    private static void checkLosses(){
        for (player p:players) {
            if (p.getCards().size() == 0){
                JOptionPane.showConfirmDialog(
                        null,
                        p.getName() +
                                " unfortunately you have lost"
                );
                players.remove(p);
            }
        }
    }
    //this method checks if a player has no cards.txt left, if they do they are removed, it also shows a scoreboard
    private static void endRound(player p){
        deck.addAll(p.getCards());
    }
    //ends the round by retrieving all of the cards.txt from the winning player's hand.
    private static boolean quit(String message){
        int choice = JOptionPane.showConfirmDialog(
                null,
                message,
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


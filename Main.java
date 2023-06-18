import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;


class Card implements Serializable {
    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}


class Player implements Serializable {
    private String name;
    private List<Card> hand;
    private Card playedCard;
    private int score =0;

    public Card getPlayedCard() {
        return playedCard;
    }

    public void setPlayedCard(Card card) {
        playedCard = card;
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void removeFromHand(int index) {
        hand.remove(index);
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public Card playCard(int index) {
        return hand.remove(index);
    }

    public List<Card> getHand() {
        return hand;
    }

    @Override
    public String toString() {
        return name;
    }

    public Object getName() {
        return this.name;
    }

    public Object getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}



// GoBoomGame class representing the game itself
class GoBoomGame implements Serializable {
    private List<Card> deck;
    private List<Player> players;
    private Card leadCard;
    private int currentPlayerIndex;
    private Map<Player, Integer> scores;
    private boolean gameRunning = true; // Flag to track the game state

    private void calculateScores() {
        for (Player player : players) {
            int score = 0;
            List<Card> hand = player.getHand();
            for (Card card : hand) {
                String rank = card.getRank();
                if (rank.equals("A")) {
                    score += 1;
                } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                    score += 10;
                } else {
                    int faceValue = Integer.parseInt(rank);
                    score += faceValue;
                }
            }
            scores.put(player, score);
        }
    }

    public GoBoomGame() {
        initializeDeck();
        initializePlayers();
        shuffleDeck();
        dealCards();
        determineLeadPlayer();
        this.scores = new HashMap<>(); // Initialize the scores variable
        this.gameRunning = true;

        currentPlayerIndex = 0;
        for (Player player : players) {
            this.scores.put(player, 0); // Initialize each player's score to 0
        }

    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    private void initializePlayers() {
        players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
        players.add(new Player("Player 4"));
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            for (Player player : players) {
                Card card = deck.remove(0);
                player.addCardToHand(card);
            }
        }
    }

    private void determineLeadPlayer() {
        leadCard = deck.remove(0);
        String leadRank = leadCard.getRank();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String playerName = player.toString();
            if ((leadRank.equals("A") || leadRank.equals("5") || leadRank.equals("9") || leadRank.equals("K")) && playerName.equals("Player 1")) {
                currentPlayerIndex = i;
                break;
            } else if ((leadRank.equals("2") || leadRank.equals("6") || leadRank.equals("10")) && playerName.equals("Player 2")) {
                currentPlayerIndex = i;
                break;
            } else if ((leadRank.equals("3") || leadRank.equals("7") || leadRank.equals("J")) && playerName.equals("Player 3")) {
                currentPlayerIndex = i;
                break;
            } else if ((leadRank.equals("4") || leadRank.equals("8") || leadRank.equals("Q")) && playerName.equals("Player 4")) {
                currentPlayerIndex = i;
                break;
            }
        }
    }

    private void playRound() {
        Player currentPlayer = players.get(currentPlayerIndex);
    
        System.out.println("\nLead Card : " + leadCard + "\n");
    
        System.out.println(currentPlayer + "'s turn.");
    
        int cardIndex = getCardIndexToPlay(currentPlayer);
        Card playedCard = currentPlayer.playCard(cardIndex);
    
        System.out.println(currentPlayer + " played " + playedCard);
    
        // Update the score based on the played card's rank
        int score = scores.get(currentPlayer);
        if (playedCard.getRank().equals("A")) {
            score += 1;
        } else if (playedCard.getRank().equals("K") || playedCard.getRank().equals("Q") || playedCard.getRank().equals("J")) {
            score += 10;
        } else {
            score += Integer.parseInt(playedCard.getRank());
        }
        scores.put(currentPlayer, score);
    
        // Store the played card for the player
        currentPlayer.setPlayedCard(playedCard);
    
        // Check if the played card matches the suit or rank of the lead card
        if (!playedCard.getSuit().equals(leadCard.getSuit()) && !playedCard.getRank().equals(leadCard.getRank())) {
            boolean hasMatchingCard = false;
    
            // Check if the current player has a card that matches the suit or rank of the lead card
            for (Card card : currentPlayer.getHand()) {
                if (card.getSuit().equals(leadCard.getSuit()) || card.getRank().equals(leadCard.getRank())) {
                    hasMatchingCard = true;
                    break;
                }
            }
    
            if (hasMatchingCard) {
                System.out.println("Invalid move! The played card must match the suit or rank of the lead card.");
                currentPlayer.addCardToHand(playedCard); // Add the card back to the player's hand
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // Move to the next player
            }
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // Move to the next player
        }
    
        if (currentPlayerIndex == 0) {
            // Determine the winner of the trick
            Player trickWinner = determineTrickWinner();
            System.out.println("\n+++ Trick Winner +++");
            System.out.println(trickWinner + " wins the trick!");
    
            // Select the new lead card
            leadCard = selectNewLeadCard(trickWinner);
    
            currentPlayerIndex = players.indexOf(trickWinner);
        }
    
        // Check if the current player has no cards left
        if (currentPlayer.getHand().isEmpty()) {
            System.out.println("\n" + currentPlayer + " has no cards left in his hand.");
            System.out.println("\n+++ Game Over +++");
            System.out.println(currentPlayer + " wins the round!!");
    
            // Set the score of the winning player to 0
            scores.put(currentPlayer, 0);
    
            // Display final scores
            System.out.println("\n+++ Final Scores +++");
            for (Player player : players) {
                System.out.println(player + "'s score : " + scores.get(player));
            }
    
            // Determine the winner based on the lowest score
            int lowestScore = Integer.MAX_VALUE;
            Player winner = null;
            for (Player player : players) {
                int playerScore = scores.get(player);
                if (playerScore < lowestScore) {
                    lowestScore = playerScore;
                    winner = player;
                }
            }
            System.out.println("\n+++ Winner +++");
            System.out.println(winner + " wins the game !!");
        }
    }
    
    
    private Player determineTrickWinner() {
        Player trickWinner = null;
        int highestValue = -1;
    
        for (Player player : players) {
            Card playedCard = player.getPlayedCard();
            if (playedCard != null) {
                int value = getCardValue(playedCard);
                if (value > highestValue) {
                    highestValue = value;
                    trickWinner = player;
                }
            }
        }
    
        return trickWinner;
    }
    
    private int getCardValue(Card card) {
        String rank = card.getRank();
        if (rank.equals("A")) {
            return 14;
        } else if (rank.equals("K")) {
            return 13;
        } else if (rank.equals("Q")) {
            return 12;
        } else if (rank.equals("J")) {
            return 11;
        } else {
            return Integer.parseInt(rank);
        }
    }
    
    private Card selectNewLeadCard(Player trickWinner) {
        List<Card> hand = trickWinner.getHand();
    
        System.out.println(trickWinner + ", select a new lead card from your hand:");
    
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ". " + hand.get(i));
        }
    
        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();
    
        if (selection >= 1 && selection <= hand.size()) {
            return hand.remove(selection - 1);
        } else {
            System.out.println("Invalid selection! Choosing the first card by default.");
            return hand.get(0);
        }

    }


    private int getCardIndexToPlay(Player currentPlayer) {
        Scanner scanner = new Scanner(System.in);
        List<Card> hand = currentPlayer.getHand();

        boolean hasMatchingCard = false;
        for (Card card : hand) {
            if (card.getSuit().equals(leadCard.getSuit()) || card.getRank().equals(leadCard.getRank())) {
                hasMatchingCard = true;
                break;
            }
        }

        if (!hasMatchingCard) {
            System.out.println("You don't have any cards that match the lead card. You must choose a card from the deck:");

            for (int i = 0; i < deck.size(); i++) {
                System.out.println((i + 1) + ": " + deck.get(i));
            }

            int cardIndex = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Enter card: ");
                if (scanner.hasNextInt()) {
                    cardIndex = scanner.nextInt();
                    if (cardIndex >= 1 && cardIndex <= deck.size()) {
                        Card chosenCard = deck.get(cardIndex - 1);
                        if (chosenCard.getSuit().equals(leadCard.getSuit()) || chosenCard.getRank().equals(leadCard.getRank())) {
                            validInput = true;
                        } else {
                            System.out.println("Invalid choice! The card must match the suit or rank of the lead card.");
                        }
                    } else {
                        System.out.println("Invalid card! Please Insert a valid card.");
                    }
                } else {
                    System.out.println("Invalid input! Please Insert a valid card.");
                    scanner.next();
                }
            }

            Card chosenCard = deck.remove(cardIndex - 1);
            currentPlayer.addCardToHand(chosenCard);

            return currentPlayer.getHand().indexOf(chosenCard);
        } else {
            System.out.println("Choose a card to play from your hand:");
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i));
            }

            int cardIndex = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Enter card: ");
                if (scanner.hasNextInt()) {
                    cardIndex = scanner.nextInt();
                    if (cardIndex >= 1 && cardIndex <= hand.size()) {
                        Card chosenCard = hand.get(cardIndex - 1);
                        if (chosenCard.getSuit().equals(leadCard.getSuit()) || chosenCard.getRank().equals(leadCard.getRank())) {
                            validInput = true;
                        } else {
                            System.out.println("Invalid choice! The chosen card must match the suit or rank of the lead card.");
                        }
                    } else {
                        System.out.println("Invalid card ! Please Insert a valid card.");
                    }
                } else {
                    System.out.println("Invalid input! Please Insert a valid card card.");
                    scanner.next();
                }
            }

            return cardIndex - 1;
        }
    }

public void saveGame() {
        Properties properties = new Properties();

        // Save deck
        properties.setProperty("deck", deckToString());

        // Save players
        int playerIndex = 1;
        for (Player player : players) {
            properties.setProperty("player" + playerIndex, playerToString(player));
            playerIndex++;
        }

        // Save lead card, current player index, and game running flag
        properties.setProperty("leadCard", cardToString(leadCard));
        properties.setProperty("currentPlayerIndex", Integer.toString(currentPlayerIndex));
        properties.setProperty("gameRunning", Boolean.toString(gameRunning));

        // Save properties to file
        try (OutputStream output = new FileOutputStream("data.txt")) {
            properties.store(output, null);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame() {
        Properties properties = new Properties();

        // Load properties from file
        try (InputStream input = new FileInputStream("data.txt")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Load deck
        String deckString = properties.getProperty("deck");
        deck = stringToDeck(deckString);

        // Load players
        players = new ArrayList<>();
        int playerIndex = 1;
        while (true) {
            String playerKey = "player" + playerIndex;
            if (properties.containsKey(playerKey)) {
                String playerString = properties.getProperty(playerKey);
                Player player = stringToPlayer(playerString);
                players.add(player);
                playerIndex++;
            } else {
                break;
            }
        }

        // Load lead card, current player index, and game running flag
        String leadCardString = properties.getProperty("leadCard");
        leadCard = stringToCard(leadCardString);
        currentPlayerIndex = Integer.parseInt(properties.getProperty("currentPlayerIndex"));
        gameRunning = Boolean.parseBoolean(properties.getProperty("gameRunning"));

        System.out.println("Game loaded successfully!");
    }

    // Utility methods for converting objects to string representations and vice versa
    // (Implement these methods based on your existing implementation)

    private String deckToString() {
        // Convert deck to string representation
        StringBuilder sb = new StringBuilder();
        for (Card card : deck) {
            sb.append(card.getSuit()).append(",").append(card.getRank()).append("\n");
        }
        return sb.toString();
    }

    private List<Card> stringToDeck(String deckString) {
        // Convert string representation to deck
        List<Card> deck = new ArrayList<>();
        String[] lines = deckString.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            String suit = parts[0];
            String rank = parts[1];
            Card card = new Card(suit, rank);
            deck.add(card);
        }
        return deck;
    }

    private String playerToString(Player player) {
        // Convert player to string representation
        StringBuilder sb = new StringBuilder();
        sb.append(player.getName()).append(",").append(player.getScore()).append("\n");
        return sb.toString();
    }

    private Player stringToPlayer(String playerString) {
        // Convert string representation to player
        String[] parts = playerString.split(",");
        String name = parts[0];
        int score = 0;

        if (parts.length > 1) {
            try {
                String scoreString = parts[1].replaceAll("\\n", "");
                score = Integer.parseInt(scoreString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Player player = new Player(name);
        player.setScore(score);
        return player;
    }


    private String cardToString(Card card) {
        // Convert card to string representation
        return card.getSuit() + "," + card.getRank();
    }

    private Card stringToCard(String cardString) {
        // Convert string representation to card
        String[] parts = cardString.split(",");
        String suit = parts[0];
        String rank = parts[1];
        return new Card(suit, rank);
    }

    public void resetGame() {
        initializeDeck();
        initializePlayers();
        shuffleDeck();
        dealCards();
        determineLeadPlayer();
        scores.clear(); // Clear the scores

        currentPlayerIndex = 0;
        for (Player player : players) {
            scores.put(player, 0); // Initialize each player's score to 0
        }

        gameRunning = true;
    }

    public void playGame() {

        int round = 1;
        int playerCount = players.size();
    
        while (gameRunning) {
            System.out.println("\n+++++ Round " + round + " +++++\n");
    
            Player currentPlayer = players.get(currentPlayerIndex); // Get the current player
    
            for (int i = 0; i < playerCount; i++) {
                playRound();
            }
    
            calculateScores(); // Calculate scores at the end of each round
    
            System.out.println("\n+++ Option +++");
            System.out.println("1. Continue to the next round");
            System.out.println("2. Save and exit the game");
            System.out.println("3. Reset the game");
    
            Scanner scanner = new Scanner(System.in);
            int option = 0;
            while (option != 1 && option != 2 && option != 3) {
                System.out.print("Kindly Choose an option: ");
                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    if (option != 1 && option != 2 && option != 3) {
                        System.out.println("Invalid option! Please choose an option between 1 or 2 or 3");
                    }
                } else {
                    System.out.println("Invalid input! Kindly insert a valid option number.");
                    scanner.next(); // Consume invalid input
                }
            }
    
            if (option == 2) {
                // Save the game and quit
                saveGame();
                return;

            } else if (option == 3) {
                // Reset the game
                resetGame();
                
                round = 1; 
                // Start from the first round

            } else {

                round++; // Proceed to the next round

                //skips to the next player
                if (currentPlayerIndex != players.size() - 1) { 
                    currentPlayerIndex++;    
                } else {
                    currentPlayerIndex = 0;
                }
            }
        }
    }

}
    public class Main {
    public static void main(String[] args) {
        GoBoomGame game = new GoBoomGame();
        System.out.println("Do you want to load a saved game? (y/n)");
      Scanner scanner = new Scanner(System.in);

        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            game.loadGame();
        } 
        game.playGame();
    }
}
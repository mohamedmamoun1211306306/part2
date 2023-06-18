import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class game2 {
    private List<Card> deck;
    private List<Card>[] playerHands;
    private Card leadCard;
    private Player leadPlayer;
    private Player currentPlayer;
    private int roundCount;
    private int trickCount;
    private Player[] players = Player.values();
    private JFrame frame;
    private JLabel leadCardLabel;
    private JLabel currentPlayerLabel;
    private JLabel[] playerScoreLabels;
    private JPanel[] playerHandPanels;
    private JButton[] cardButtons;
    private JButton exitButton;
    private JLabel deckLabel;
    private JPanel centerPanel;
    private JButton deckButton;
    private JLabel drawnCardLabel;
    private JLabel turnLabel; // New label to display the current player's turn
    private int[] scores;

    public game2() {
        deck = new ArrayList<>();
        playerHands = new List[players.length];
        for (int i = 0; i < players.length; i++) {
            playerHands[i] = new ArrayList<>();
        }
        roundCount = 1;
        trickCount = 1;
        scores = new int[players.length];

        initializeDeck();
        shuffleDeck();
        dealCards();

        leadCard = deck.get(0);
        determineLeadPlayer();

        initializeGUI();
        updateGUI();
    }

    enum Suit {
        SPADES('♠'), CLUBS('♣'), HEARTS('♥'), DIAMONDS('♦');

        private char symbol;

        Suit(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    enum Rank {
        ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13);

        private int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    class Card {
        private Suit suit;
        private Rank rank;
        private boolean facedUp;

        public Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
            this.facedUp = true;
        }

        public Suit getSuit() {
            return suit;
        }

        public Rank getRank() {
            return rank;
        }

        public boolean isFacedUp() {
            return facedUp;
        }

        public void setFacedUp(boolean facedUp) {
            this.facedUp = facedUp;
        }

        @Override
        public String toString() {
            return rank.getValue() + " of " + suit.getSymbol();
        }
    }

    enum Player {
        PLAYER1, PLAYER2, PLAYER3, PLAYER4;

        private int score;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        int cardsPerPlayer = 7;

        for (int i = 0; i < cardsPerPlayer; i++) {
            for (int j = 0; j < players.length; j++) {
                playerHands[j].add(deck.remove(0));
            }
        }
    }

    private void determineLeadPlayer() {
        for (Player player : players) {
            if (playerHasLeadCard(player)) {
                leadPlayer = player;
                currentPlayer = player;
                break;
            }
        }
    }

    private boolean playerHasLeadCard(Player player) {
        for (Card card : playerHands[player.ordinal()]) {
            if (card.getSuit() == leadCard.getSuit() &&
                    (card.getRank() == Rank.ACE || card.getRank() == Rank.FIVE ||
                            card.getRank() == Rank.NINE || card.getRank() == Rank.KING)) {
                return true;
            }
        }
        return false;
    }

    private void playCard(Player player, Card card) {
        if (player != currentPlayer) {
            JOptionPane.showMessageDialog(frame, "Not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isCardValid(card)) {
            JOptionPane.showMessageDialog(frame, "Invalid card!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        playerHands[player.ordinal()].remove(card);

        if (playerHands[player.ordinal()].isEmpty()) {
            endRound();
        } else {
            currentPlayer = getNextPlayer(player);
            trickCount++;
            updateGUI();
        }
    }

    private boolean isCardValid(Card card) {
        if (card.getSuit() != leadCard.getSuit()) {
            for (Card c : playerHands[currentPlayer.ordinal()]) {
                if (c.getSuit() == leadCard.getSuit()) {
                    return false;
                }
            }
        }

        return true;
    }

    private Player getNextPlayer(Player currentPlayer) {
        int currentPlayerIndex = currentPlayer.ordinal();
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.length;
        return players[nextPlayerIndex];
    }

    private void endRound() {
        calculateScores();
        displayScores();

        resetRound();
        roundCount++;

        updateGUI();
    }

    private void calculateScores() {
        for (Player player : players) {
            List<Card> hand = playerHands[player.ordinal()];
            int score = 0;

            for (Card card : hand) {
                score += card.getRank().getValue();
            }

            scores[player.ordinal()] = score;
        }
    }

    private void displayScores() {
        System.out.println("Scores:");
        for (Player player : players) {
            System.out.println("Player " + player.ordinal() + ": " + scores[player.ordinal()]);
        }
        System.out.println();
    }

    private void resetRound() {
        deck.clear();
        for (List<Card> hand : playerHands) {
            hand.clear();
        }

        initializeDeck();
        shuffleDeck();
        dealCards();

        leadCard = deck.get(0);
        determineLeadPlayer();

        trickCount = 1;
    }

    private void initializeGUI() {
        frame = new JFrame("Go Boom Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0x006400)); // Dark Green

        JPanel gamePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gamePanel.setBackground(new Color(0x006400)); // Dark Green

        JPanel[] playerPanels = new JPanel[players.length];
        playerScoreLabels = new JLabel[players.length];
        playerHandPanels = new JPanel[players.length];
        cardButtons = new JButton[players.length];

        leadCardLabel = new JLabel();
        currentPlayerLabel = new JLabel();
        deckLabel = new JLabel();
        centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0x006400)); // Dark Green

        for (int i = 0; i < players.length; i++) {
            playerPanels[i] = new JPanel(new BorderLayout());
            playerPanels[i].setBackground(new Color(0x006400)); // Dark Green
            playerScoreLabels[i] = new JLabel();
            playerScoreLabels[i].setFont(playerScoreLabels[i].getFont().deriveFont(Font.BOLD));
            playerHandPanels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
            cardButtons[i] = new JButton();

            playerScoreLabels[i].setText("Player " + (i + 1) + ": " + scores[i]);
            playerPanels[i].add(playerScoreLabels[i], BorderLayout.NORTH);
            playerPanels[i].add(playerHandPanels[i], BorderLayout.CENTER);
            playerPanels[i].add(cardButtons[i], BorderLayout.SOUTH);

            gamePanel.add(playerPanels[i]);
        }

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(Color.LIGHT_GRAY);
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(leadCardLabel);

        JPanel deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deckPanel.setBackground(new Color(0x006400)); // Dark Green
        deckPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        deckPanel.add(new JLabel("Deck:"));
        deckPanel.add(deckLabel);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(Box.createVerticalGlue());

        deckButton = new JButton("Deck");
        drawnCardLabel = new JLabel();
        deckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawCard();
            }
        });
        centerPanel.add(deckButton);
        centerPanel.add(drawnCardLabel);
        centerPanel.add(Box.createVerticalGlue());

        turnLabel = new JLabel("Turn: Player 1"); // New label to display the current player's turn
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.BOLD, 16));
        turnLabel.setForeground(Color.WHITE);
        centerPanel.add(turnLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(exitButton, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.WEST);
        mainPanel.add(deckPanel, BorderLayout.EAST);

        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setVisible(true);

        for (int i = 0; i < players.length; i++) {
            final int playerIndex = i;
            cardButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Card selectedCard = selectCard(playerIndex);
                    if (selectedCard != null) {
                        playCard(players[playerIndex], selectedCard);
                    }
                }
            });
        }
    }

    private Card selectCard(int playerIndex) {
        List<Card> hand = playerHands[playerIndex];
        Object[] options = hand.toArray();

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        for (Card card : hand) {
            JButton cardButton = new JButton(card.getRank().getValue() + " of " + card.getSuit().getSymbol());
            cardButton.setPreferredSize(new Dimension(80, 120));
            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose(); // Close the custom dialog
                }
            });
            cardPanel.add(cardButton);
        }

        int result = JOptionPane.showConfirmDialog(null, cardPanel, "Select a card to play:",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.CLOSED_OPTION) {
            return null; // User closed the dialog without selecting a card
        } else {
            int selectedIndex = -1;
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i) == options[result]) {
                    selectedIndex = i;
                    break;
                }
            }
            return hand.remove(selectedIndex);
        }
    }

    private void drawCard() {
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(deck.size() - 1);
            centerPanel.remove(deckButton);
            drawnCardLabel.setText("Drawn Card: " + drawnCard.toString());
            centerPanel.revalidate();
            centerPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(frame, "The deck is empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGUI() {
        currentPlayerLabel.setText("Current Player: " + currentPlayer.ordinal());
        leadCardLabel.setText("Lead Card: " + leadCard.toString());
        deckLabel.setText("Deck: " + deck.size() + " cards");
        turnLabel.setText("Turn: Player " + (currentPlayer.ordinal() + 1)); // Update the current player's turn

        for (int i = 0; i < players.length; i++) {
            List<Card> hand = playerHands[i];
            playerScoreLabels[i].setText("Player " + (i + 1) + ": " + scores[i]);

            playerHandPanels[i].removeAll();
            JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            for (Card card : hand) {
                JButton cardButton = new JButton(card.getRank().getValue() + " of " + card.getSuit().getSymbol());
                cardButton.setPreferredSize(new Dimension(80, 120));
                int playerIndex = i; // Store the player index in a separate variable
                cardButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Card selectedCard = card;
                        if (currentPlayer == players[playerIndex]) {
                            playCard(players[playerIndex], selectedCard);
                        }
                    }
                });
                cardPanel.add(cardButton);
            }
            playerHandPanels[i].add(cardPanel);
        }

        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new game2();
            }
        });
    }
}
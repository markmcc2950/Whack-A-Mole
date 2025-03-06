import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Random;
import javax.swing.*;

public class WhackAMole {
    // Set up toolkit stuff
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    double screenWidth = screenSize.getWidth();
    double screenHeight = screenSize.getHeight();

    // Initialize the custom cursor (properly set in the constructor with the rest of the frame)
    Image image = toolkit.getImage(getClass().getResource("/HaloHammer.png"));
    Cursor customCursor = toolkit.createCustomCursor(image, new Point(0, 0), "Custom Cursor");

    // -----------------------------------------------------------------------------------------------------------------
    int scorePadding = 50;
    int boardWidth = (int)screenHeight - scorePadding;
    int boardHeight = (int)screenHeight - scorePadding;

    int squareSize = 20;
    int numOfRows = squareSize;
    int numOfColumns = squareSize;
    int numOfButtons = numOfRows * numOfColumns;

    int iconWidth = ((int)screenHeight / (numOfButtons / numOfRows)) / 2;
    int iconHeight = ((int)screenHeight / (numOfButtons / numOfColumns)) / 2;

    int score = 0;
    Boolean gameIsActive = true;

    JFrame frame = new JFrame("Whack-A-Mole");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[] board = new JButton[numOfButtons];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    // Designate how many moles and plants will exist based on the number of tiles on the board
    int numOfMoles = (numOfButtons / 16) + 1;
    int numOfPlants = (numOfButtons / 12) + 1;

    JButton[] currMoleTile = new JButton[numOfMoles];
    JButton[] currPlantTile = new JButton[numOfPlants];

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    Timer gameOverTimer;

    // Create a hashmap to track which block are being used
    HashMap<JButton, Boolean> occupiedSlots = new HashMap<JButton, Boolean>();


    // Creating a constructor
    WhackAMole() {
        for (int i = 0; i < numOfButtons; i++) {
            occupiedSlots.put(board[i], false);
        }

        // Set up the frame
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setCursor(customCursor);

        // Set up text
        textLabel.setFont(new Font("Kokila Bold Italic", Font.PLAIN, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: 0");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.SOUTH);

        boardPanel.setLayout(new GridLayout(numOfRows, numOfColumns));
        boardPanel.setBackground(Color.DARK_GRAY);
        frame.add(boardPanel);

        // Set up Piranha Plant Icon
        Image plantImg = new ImageIcon(getClass().getResource("./AngryBadger.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_SMOOTH));

        // Set up Monty Mole Icon
        Image moleImg = new ImageIcon(getClass().getResource("./CartoonMole.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_SMOOTH));

        // Create the clickable buttons
        for (int i = 0; i < numOfButtons; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.setBackground(Color.white);

            tile.addActionListener((new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton tile = (JButton) e.getSource();
                    for (int j = 0; j < numOfMoles; j++) {
                        if (tile == currMoleTile[j] && gameIsActive && currMoleTile[j] != null) {
                            currMoleTile[j].setEnabled(false);
                            score += 10;
                            textLabel.setText("Score: " + Integer.toString(score));
                        }
                    }

                    for (int k = 0; k < numOfPlants; k++) {
                        if (tile == currPlantTile[k] && gameIsActive) {
                            setMoleTimer.stop();
                            setPlantTimer.stop();
                            gameIsActive = false;

                            int failTimer = 1500 / (squareSize * squareSize);

                            gameOverTimer = new Timer(failTimer, new ActionListener() {
                                int buttonCtr = 0;
                                float redColor = 1;
                                float blueColor = 0;
                                float greenColor = 0;

                                public void actionPerformed(ActionEvent e) {
                                    board[buttonCtr].setBackground(new Color(redColor, greenColor, blueColor));
                                    board[buttonCtr].setEnabled(false);

                                    buttonCtr++;
                                    redColor = redColor - (float) ((float) 1.0 / numOfButtons);

                                    if (buttonCtr >= numOfButtons) {
                                        textLabel.setText("Game Over! Total Score: " + score);
                                        gameOverTimer.stop();
                                    }
                                }
                            });

                            gameOverTimer.start();
                        }
                    }
                }
            }));
        }

        int moleTimer = 450 + (10 * squareSize);
        int plantTimer = 550 + (10 * squareSize);

        // Set a new timer for the mole icon for every 1,000ms or 1 second
        setMoleTimer = new Timer(moleTimer, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < numOfMoles; i++) {
                    System.out.println("DEBUG: Setting moles.");
                    // If a button is already assigned to the mole, clear it
                    if (currMoleTile[i] != null) {
                        currMoleTile[i].setBackground(Color.white);
                        currMoleTile[i].setIcon(null);
                        occupiedSlots.put(currMoleTile[i], false);
                        currMoleTile[i] = null;

                    }
                    int num = random.nextInt(numOfButtons);

                    // If the tile selected is already used, change the current num value
                    if (occupiedSlots.get(board[num]) != null) {
                        System.out.println("DEBUG: occupiedSlots val (mole): " + occupiedSlots.get(board[num]));
                        while (occupiedSlots.get(board[num]) != null && occupiedSlots.get(board[num])) {
                            num = random.nextInt(numOfButtons);
                        }
                    }

                    JButton tile = board[num];

                    currMoleTile[i] = tile;
                    currMoleTile[i].setBackground(Color.yellow);
                    currMoleTile[i].setIcon(moleIcon);
                    occupiedSlots.put(currMoleTile[i], true);

                    currMoleTile[i].setEnabled(true);
                }
            }
        });

        // Set a new timer for the piranha icon for every 1,000ms or 1 second
        setPlantTimer = new Timer(plantTimer, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < numOfPlants; i++) {
                    System.out.println("DEBUG: Setting plants.");
                    // If a button is already assigned to the plant, clear it
                    if (currPlantTile[i] != null) {
                        currPlantTile[i].setBackground(Color.white);
                        currPlantTile[i].setIcon(null);
                        occupiedSlots.put(currPlantTile[i], false);

                        currPlantTile[i] = null;
                    }

                    int num = random.nextInt(numOfButtons);

                    // If the tile selected is already used, change the current num value
                    if (occupiedSlots.get(board[num]) != null) {
                        System.out.println("DEBUG: occupiedSlots val (plant): " + occupiedSlots.get(board[num]));
                        while (occupiedSlots.get(board[num]) != null && occupiedSlots.get(board[num])) {
                            num = random.nextInt(numOfButtons);
                        }
                    }

                    JButton tile = board[num];

                    currPlantTile[i] = tile;
                    currPlantTile[i].setBackground(Color.red);
                    currPlantTile[i].setIcon(plantIcon);
                    occupiedSlots.put(currPlantTile[i], true);

                    currPlantTile[i].setEnabled(true);
                }
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();

        // Set the frame to visible only after everything is set up
        frame.setVisible(true);
    }
}





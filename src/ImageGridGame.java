import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageGridGame extends JFrame {
    private int level = 1;
    private int gridSize = 1; // Initial gridSize
    private Image image;
    private Color[][] colors;
    private boolean startPage = true;
    private String[] imagePaths = {"Creation of Adam.jpg", "Mona Lisa.jpg", "Starry Night.jpg", "The Last Supper.jpg", "The Persistence of Memory.jpg", "The Scream.jpg"};
    private JPanel levelButtonPanel;
    private JTextField inputField;
    private String correctImagePath;
    private boolean drawTextBox = false; // Flag to track whether to draw the text box

    public ImageGridGame() {
        // Set up the JFrame
        setTitle("Image Grid Game");
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 30));

        setSize(800, 600); // Set a fixed size for the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startPage) {
                    // Load and draw the image
                    try {
                        BufferedImage homeImage = ImageIO.read(new File("/Users/esmecampbell/Downloads/Hackathon2024/src/HomeImage.png"));
                        g.drawImage(homeImage, 0, 0, getWidth(), getHeight(), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Draw image grid
                    int squareSize = image.getWidth(null) / gridSize;
                    for (int i = 0; i < gridSize; i++) {
                        for (int j = 0; j < gridSize; j++) {
                            g.setColor(colors[i][j]);
                            g.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                        }
                    }

                    // Draw the text box only if the flag is set to true
                    if (drawTextBox) {
                        g.setColor(Color.WHITE);
                        g.fillRect(600, 500, 200, 60); // Increase the height to accommodate multiple lines

                        // Draw text inside the text box
                        g.setColor(Color.BLACK); // Set color for text
                        String text = "INSTRUCTIONS:\n" +
                                "\n" +
                                "Click each level on the top to see more detail\n\n" +
                                "Try to guess the painting on the lowest level possible!\n" +
                                "\n" +
                                "Choose an option from the word bank\n" +
                                "Then type it into the input box to the right\n" +
                                "\n" +
                                "\n" +
                                "WORD BANK:\n" +
                                "\n" +
                                "Mona Lisa\n" +
                                "Starry Night\n" +
                                "Dali\n" +
                                "The Last Supper\n" +
                                "Girl With a Pearl Earring\n" +
                                "Creation of Adam\n" +
                                "The Scream\n" +
                                "Irises\n" +
                                "The Great Wave off Kanagawa\n" +
                                "The Night Watch\n" +
                                "The Birth of Venus\n" +
                                "The  Kiss";
                        Font font = new Font("Arial", Font.PLAIN, 13); // Set font for text
                        g.setFont(font);
                        int textX = 280; // X-coordinate for text
                        int textY = 40; // Y-coordinate for the first line of text
                        String[] lines = text.split("\n"); // Split the text into lines
                        for (String line : lines) {
                            g.drawString(line, textX, textY);
                            textY += g.getFontMetrics().getHeight(); // Move to the next line
                        }
                    }

                }
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (startPage) {
                    startGame(panel);
                    inputField.requestFocusInWindow();
                }
            }
        });
        add(panel);

        // Create level buttons panel but hide it initially
        levelButtonPanel = new JPanel(new GridLayout(1, 8));
        for (int i = 1; i <= 8; i++) {
            JButton levelButton = new JButton("Level " + i);
            int finalI = i;

            levelButton.addActionListener(e -> {
                level = finalI;
                gridSize = (int) Math.pow(2, level); // Update gridSize based on level
                colors = divideImageIntoSquares(image, gridSize); // Update colors array
                panel.repaint(); // Redraw the panel
                addInputField(); // Add input field after level selection
                inputField.requestFocusInWindow(); // Ensure input field gets focus after level selection

            });
            levelButtonPanel.add(levelButton);
        }
        levelButtonPanel.setVisible(false);
        add(levelButtonPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    private void startGame(JPanel panel) {
        startPage = false; // Change start page flag
        loadImage(); // Load the image
        gridSize = (int) Math.pow(2, level); // Update gridSize based on level
        colors = divideImageIntoSquares(image, gridSize); // Update colors array
        panel.repaint(); // Redraw the panel

        // Request focus for the panel to allow further clicks
        panel.requestFocusInWindow();

        // Show level buttons
        levelButtonPanel.setVisible(true);

        // Add input field after level selection
        addInputField();
        // Ensure input field gets focus
        inputField.requestFocusInWindow();
    }

    public static String removeLastNCharacters(String str, int n) {
        return (str == null || str.isEmpty()) ? str : str.substring(0, Math.max(0, str.length() - n));
    }

    private void addInputField() {

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredText = inputField.getText().trim(); // Trim whitespace
                if (!enteredText.isEmpty()) { // Check if input is not empty
                    if (enteredText.equals(correctImagePath)) {
                        try {
                            // Load and display the image at the specified path
                            BufferedImage correctImage = ImageIO.read(new File("/Users/esmecampbell/Downloads/Hackathon2024/src/CERTIFIED ARTISTIC GENIUS.png"));
                            JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(correctImage)), "Correct!", JOptionPane.PLAIN_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else {
                        System.out.println("Incorrect!");
                    }
                } else {
                    System.out.println("Please enter something!");
                }
                // Clear the input field after processing
                inputField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inputPanel.add(inputField);
        add(inputPanel, BorderLayout.EAST);
        revalidate();
        drawTextBox = true; // Set the flag to true after adding the input field
    }

    private Color[][] divideImageIntoSquares(Image image, int gridSize) {
        BufferedImage bufferedImage = (BufferedImage) image;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int squareSize = width / gridSize;
        Color[][] colors = new Color[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int startX = i * squareSize;
                int startY = j * squareSize;
                int endX = Math.min(startX + squareSize, width); // Ensure endX is within bounds
                int endY = Math.min(startY + squareSize, height); // Ensure endY is within bounds

                // Calculate average color for the mini square
                int redSum = 0, greenSum = 0, blueSum = 0;
                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        Color color = new Color(bufferedImage.getRGB(x, y));
                        redSum += color.getRed();
                        greenSum += color.getGreen();
                        blueSum += color.getBlue();
                    }
                }
                int numPixels = (endX - startX) * (endY - startY); // Update numPixels calculation
                int avgRed = redSum / numPixels;
                int avgGreen = greenSum / numPixels;
                int avgBlue = blueSum / numPixels;
                colors[i][j] = new Color(avgRed, avgGreen, avgBlue);
            }
        }
        return colors;
    }

    private void loadImage() {
        Random random = new Random();
        String randomImagePath = imagePaths[random.nextInt(imagePaths.length)]; // Randomly select an image path
        String imagePath = "/Users/esmecampbell/Downloads/Hackathon2024/src/" + randomImagePath; // Assuming the images are in the same directory as the class file
        correctImagePath = removeLastNCharacters(randomImagePath,4);
        try {
            image = ImageIO.read(new File(imagePath));
            if (image != null) { // Ensure image is loaded successfully
                // Now you have loaded the image, you can proceed with your logic
                // For example, pass it to your divideImageIntoSquares method
            } else {
                System.err.println("Failed to load image: " + imagePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageGridGame::new);
    }
}

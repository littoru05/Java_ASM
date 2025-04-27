import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ButtonManager {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JButton playButton, quitButton, pauseButton, continueButton, exitButton, restartButton, homeButton, optionButton;
    private JButton map1Button, map2Button, backButton, backOptionButton;
    private JButton selectButton, cancelButton, spaceshipButton, levelButton;
    private JButton[] shipButtons;
    private JButton[] shipSelectionButtons;
    private JButton easyButton, normalButton, hardButton;
    private JButton[] levelSelectionButtons;
    private BufferedImage[] shipOptions;
    private GamePanel gamePanel;
    private int selectedShipIndex = 0;

    private BufferedImage playImage, optionImage, quitImage, pauseImage, continueImage, exitImage, restartImage, homeImage;
    private BufferedImage map1Image, map2Image, backImage, spaceshipImage, selectImage, cancelImage;
    private BufferedImage levelImage, easyImage, normalImage, hardImage;

    private int buttonWidth = 125;
    private int buttonHeight = 50;
    private int spacing = 30;

    public ButtonManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadImages();
        createButtons();
        createOptionButtons();
        createMapSelectionButtons();
        createEndButtons();
        createShipSelectionMenu();
        createLevelSelectionMenu();
    }

    private void loadImages() {
        playImage = loadImage("/image/play.png");
        optionImage = loadImage("/image/option.png");
        quitImage = loadImage("/image/quit.png");
        pauseImage = loadImage("/image/pause.png");
        continueImage = loadImage("/image/continue.png");
        exitImage = loadImage("/image/exit.png");
        restartImage = loadImage("/image/restart.png");
        homeImage = loadImage("/image/home.png");
        map1Image = loadImage("/image/map1.png");
        map2Image = loadImage("/image/map2.png");
        backImage = loadImage("/image/back.png");
        spaceshipImage = loadImage("/image/spaceshipselect.png");
        selectImage = loadImage("/image/select.png");
        cancelImage = loadImage("/image/cancel.png");
        levelImage = loadImage("/image/level.png");
        easyImage = loadImage("/image/easy.png");
        normalImage = loadImage("/image/normal.png");
        hardImage = loadImage("/image/hard.png");
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private void styleButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void createButtons() {
        ImageIcon playIcon = new ImageIcon(playImage);
        playButton = new JButton(scaleIcon(playIcon, buttonWidth, buttonHeight));
        styleButton(playButton);
        playButton.setBounds((WIDTH - 125) / 2, HEIGHT - 240, buttonWidth, buttonHeight);
        playButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.SELECTMAP);
            toggleMenuButtons(false);
            toggleMapButtons(true);
        });

        ImageIcon optionIcon = new ImageIcon(optionImage);
        optionButton = new JButton(scaleIcon(optionIcon, buttonWidth, buttonHeight));
        styleButton(optionButton);
        optionButton.setBounds((WIDTH - 125) / 2 - 2, HEIGHT - 170, 125, 50);
        optionButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.OPTION);
            toggleMenuButtons(false);
            toggleOptionButtons(true);
        });

        ImageIcon quitIcon = new ImageIcon(quitImage);
        quitButton = new JButton(scaleIcon(quitIcon, buttonWidth , buttonHeight ));
        styleButton(quitButton);
        quitButton.setBounds((WIDTH - 125) / 2 + 4, HEIGHT - 100, 120, 45);
        quitButton.addActionListener(e -> System.exit(0));

        ImageIcon pauseIcon = new ImageIcon(pauseImage);
        pauseButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                int d = Math.min(getWidth(), getHeight());
                g.setClip(0, 0, d, d);
                super.paintComponent(g);
            }
        };
        pauseButton.setIcon(scaleIcon(pauseIcon, 75, 75));
        styleButton(pauseButton);
        pauseButton.setBounds(WIDTH - 100, -15, 125, 125);
        pauseButton.addActionListener(e -> gamePanel.paused());

        ImageIcon continueIcon = new ImageIcon(continueImage);
        continueButton = new JButton(scaleIcon(continueIcon, buttonWidth, buttonHeight));
        styleButton(continueButton);
        continueButton.setBounds(WIDTH / 2 - buttonWidth - spacing / 2, HEIGHT / 2 + 50, buttonWidth, buttonHeight);
        continueButton.addActionListener(e -> {
            if (gamePanel.getCurrentState() == GameState.PAUSED) {
                gamePanel.setCurrentState(GameState.PLAYING);
            }
            togglePauseButtons(false);
        });

        ImageIcon exitIcon = new ImageIcon(exitImage);
        exitButton = new JButton(scaleIcon(exitIcon, buttonWidth, buttonHeight));
        styleButton(exitButton);
        exitButton.setBounds(WIDTH / 2 + spacing / 2, HEIGHT / 2 + 50, buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> {
            gamePanel.stopGameTimer();
            gamePanel.initGameState();
            gamePanel.setCurrentState(GameState.HOME);
            togglePauseButtons(false);
            toggleMenuButtons(true);
            toggleEndButtons(false);
            gamePanel.repaint();
            gamePanel.startGameLoop();
        });

        gamePanel.add(playButton);
        gamePanel.add(optionButton);
        gamePanel.add(quitButton);
        gamePanel.add(pauseButton);
        gamePanel.add(continueButton);
        gamePanel.add(exitButton);
    }

    private void createEndButtons(){
        ImageIcon restartIcon = new ImageIcon(restartImage);
        restartButton = new JButton(scaleIcon(restartIcon, buttonWidth + 10, buttonHeight + 10));
        styleButton(restartButton);
        restartButton.setBounds(WIDTH / 2 - spacing - 120, (HEIGHT - 100) / 2 + 100, buttonWidth, buttonHeight);
        restartButton.addActionListener(e -> {
            toggleEndButtons(true);
            togglePauseButtons(false);
            toggleMenuButtons(false);
            gamePanel.restartGame();
            gamePanel.setCurrentState(GameState.PLAYING);
        });

        ImageIcon homeIcon = new ImageIcon(homeImage);
        homeButton = new JButton(scaleIcon(homeIcon, buttonWidth, buttonHeight));
        styleButton(homeButton);
        homeButton.setBounds(WIDTH / 2 + spacing, (HEIGHT - 100) / 2 + 100, buttonWidth, buttonHeight);
        homeButton.addActionListener(e -> {
            toggleEndButtons(true);
            togglePauseButtons(false);
            toggleMenuButtons(false);
            gamePanel.setCurrentState(GameState.HOME);
        });
        gamePanel.add(restartButton);
        gamePanel.add(homeButton);
    }

    private void createMapSelectionButtons() {
        ImageIcon map1Icon = new ImageIcon(map1Image);
        map1Button = new JButton(scaleIcon(map1Icon, buttonWidth, buttonHeight));
        styleButton(map1Button);
        map1Button.setBounds(WIDTH / 2 - 165, HEIGHT / 2 - 50, buttonWidth, buttonHeight);
        map1Button.addActionListener(e -> {
            gamePanel.setSelectedMap(0);
            gamePanel.initGameState();
            gamePanel.setCurrentState(GameState.PLAYING);
            toggleMapButtons(false);
        });

        ImageIcon map2Icon = new ImageIcon(map2Image);
        map2Button = new JButton(scaleIcon(map2Icon, buttonWidth, buttonHeight));
        styleButton(map2Button);
        map2Button.setBounds(WIDTH / 2 + 35, HEIGHT / 2 - 50, buttonWidth, buttonHeight);
        map2Button.addActionListener(e -> {
            gamePanel.setSelectedMap(1);
            gamePanel.initGameState();
            gamePanel.setCurrentState(GameState.PLAYING);
            toggleMapButtons(false);
        });

        gamePanel.add(map1Button);
        gamePanel.add(map2Button);
    }

    private void createOptionButtons() {
        int buttonWidth = 150;
        int buttonHeight = 50;
        int startY = HEIGHT / 2 - 100;

        ImageIcon spaceshipIcon = new ImageIcon(spaceshipImage);
        spaceshipButton = new JButton(scaleIcon(spaceshipIcon, buttonWidth, buttonHeight));
        styleButton(spaceshipButton);
        spaceshipButton.setBounds((WIDTH - buttonWidth) / 2, startY + 50, buttonWidth + 10, buttonHeight);
        spaceshipButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.SELECTSS);
            toggleOptionButtons(false);
            toggleShipSelectionButtons(true);
        });

        ImageIcon levelIcon = new ImageIcon(levelImage);
        levelButton = new JButton(scaleIcon(levelIcon, buttonWidth, buttonHeight));
        styleButton(levelButton);
        levelButton.setBounds((WIDTH - buttonWidth) / 2 + 3, startY + 100, buttonWidth + 10, buttonHeight);
        levelButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.LEVEL);
            toggleOptionButtons(false);
            toggleLevelSelectionButtons(true);
        });

        ImageIcon backIcon = new ImageIcon(backImage);
        backOptionButton = new JButton(scaleIcon(backIcon, buttonWidth, buttonHeight));
        styleButton(backOptionButton);
        backOptionButton.setBounds((WIDTH - buttonWidth) / 2 , startY + 160, buttonWidth - 5, buttonHeight - 5);
        backOptionButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.HOME);
            toggleOptionButtons(false);
            toggleMenuButtons(true);
        });

        gamePanel.add(spaceshipButton);
        gamePanel.add(levelButton);
        gamePanel.add(backOptionButton);
    }

    private void createShipSelectionMenu() {
        shipOptions = new BufferedImage[] {
            loadImage("/image/spaceship.png"),
            loadImage("/image/spaceship1.png"),
            loadImage("/image/spaceship2.png")
        };

        shipButtons = new JButton[shipOptions.length];
        for (int i = 0; i < shipOptions.length; i++) {
            final int index = i;
            shipButtons[i] = new JButton(new ImageIcon(shipOptions[i].getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            shipButtons[i].setBounds(WIDTH / 2 - 150 + i * 120, HEIGHT / 2 - 50, 100, 100);
            shipButtons[i].addActionListener(e -> {
                selectedShipIndex = index;
                updateShipSelectionUI();
            });
            gamePanel.add(shipButtons[i]);
            shipButtons[i].setVisible(false);
        }

        ImageIcon selectIcon = new ImageIcon(selectImage);
        selectButton = new JButton(scaleIcon(selectIcon, buttonWidth + 10, buttonHeight + 10));
        styleButton(selectButton);
        selectButton.setBounds(WIDTH / 2 - 120, HEIGHT / 2 + 75, buttonWidth + 10, buttonHeight + 10);
        selectButton.addActionListener(e -> {
            gamePanel.setPlaneImage(shipOptions[selectedShipIndex]);
            gamePanel.setCurrentState(GameState.OPTION);
            toggleShipSelectionButtons(false);
            toggleOptionButtons(true);
        });
        gamePanel.add(selectButton);
        selectButton.setVisible(false);

        ImageIcon cancelIcon = new ImageIcon(cancelImage);
        cancelButton = new JButton(scaleIcon(cancelIcon, buttonWidth, buttonHeight));
        styleButton(cancelButton);
        cancelButton.setBounds(WIDTH / 2 + 20, HEIGHT / 2 + 79, buttonWidth, buttonHeight);
        cancelButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.OPTION);
            toggleShipSelectionButtons(false);
            toggleOptionButtons(true);
        });
        gamePanel.add(cancelButton);
        cancelButton.setVisible(false);

        shipSelectionButtons = new JButton[] {
            shipButtons[0], shipButtons[1], shipButtons[2],
            selectButton, cancelButton
        };
    }

    private void createLevelSelectionMenu() {
        int buttonWidth = 125;
        int buttonHeight = 40;

        ImageIcon easyIcon = new ImageIcon(easyImage);
        easyButton = new JButton(scaleIcon(easyIcon, buttonWidth - 2, buttonHeight));
        styleButton(easyButton);
        easyButton.setBounds(WIDTH / 2 - 40, HEIGHT / 2  - 60, buttonWidth , buttonHeight);
        easyButton.addActionListener(e -> {
            gamePanel.setGameLevel(GameLevel.EASY);
            gamePanel.setCurrentState(GameState.OPTION);
            toggleLevelSelectionButtons(false);
            toggleOptionButtons(true);
        });

        ImageIcon normalIcon = new ImageIcon(normalImage);
        normalButton = new JButton(scaleIcon(normalIcon, buttonWidth, buttonHeight));
        styleButton(normalButton);
        normalButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 20, buttonWidth + 3, buttonHeight);
        normalButton.addActionListener(e -> {
            gamePanel.setGameLevel(GameLevel.NORMAL);
            gamePanel.setCurrentState(GameState.OPTION);
            toggleLevelSelectionButtons(false);
            toggleOptionButtons(true);
        });

        ImageIcon hardIcon = new ImageIcon(hardImage);
        hardButton = new JButton(scaleIcon(hardIcon, buttonWidth, buttonHeight));
        styleButton(hardButton);
        hardButton.setBounds(WIDTH / 2 - 43 , HEIGHT / 2 + 20, buttonWidth, buttonHeight);
        hardButton.addActionListener(e -> {
            gamePanel.setGameLevel(GameLevel.HARD);
            gamePanel.setCurrentState(GameState.OPTION);
            toggleLevelSelectionButtons(false);
            toggleOptionButtons(true);
        });

        ImageIcon backIcon = new ImageIcon(backImage);
        backButton = new JButton(scaleIcon(backIcon, buttonWidth, buttonHeight));
        styleButton(backButton);
        backButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 + 80, buttonWidth, buttonHeight);
        backButton.addActionListener(e -> {
            gamePanel.setCurrentState(GameState.OPTION);
            toggleLevelSelectionButtons(false);
            toggleOptionButtons(true);
        });

        gamePanel.add(easyButton);
        gamePanel.add(normalButton);
        gamePanel.add(hardButton);
        gamePanel.add(backButton);

        levelSelectionButtons = new JButton[] {
            easyButton, normalButton, hardButton, backButton
        };
    }

    private void updateShipSelectionUI() {
        for (int i = 0; i < shipButtons.length; i++) {
            if (i == selectedShipIndex) {
                shipButtons[i].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
            } else {
                shipButtons[i].setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

    public void toggleMenuButtons(boolean visible) {
        quitButton.setVisible(visible && gamePanel.getCurrentState() == GameState.HOME);
        playButton.setVisible(visible && gamePanel.getCurrentState() == GameState.HOME);
        optionButton.setVisible(visible && gamePanel.getCurrentState() == GameState.HOME);
        pauseButton.setVisible(visible && gamePanel.getCurrentState() == GameState.PLAYING);
    }

    public void toggleMapButtons(boolean visible) {
        map1Button.setVisible(visible);
        map2Button.setVisible(visible);
    }

    public void toggleOptionButtons(boolean visible) {
        spaceshipButton.setVisible(visible);
        levelButton.setVisible(visible);
        backOptionButton.setVisible(visible);
        pauseButton.setVisible(false);
    }

    public void toggleShipSelectionButtons(boolean visible) {
        for (JButton btn : shipSelectionButtons) {
            btn.setVisible(visible);
        }
    }

    public void toggleLevelSelectionButtons(boolean visible) {
        for (JButton btn : levelSelectionButtons) {
            btn.setVisible(visible);
        }
    }

    public void togglePauseButtons(boolean visible) {
        continueButton.setVisible(visible);
        exitButton.setVisible(visible);
    }

    public void toggleEndButtons(boolean visible) {
        restartButton.setVisible(visible);
        homeButton.setVisible(visible);
        pauseButton.setVisible(false);
    }
}
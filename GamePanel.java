import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

enum GameState {
    HOME,
    OPTION,
    SELECTMAP,
    SELECTSS,
    PLAYING,
    PAUSED,
    GAMEOVER,
    ENDGAME,
    LEVEL
}

enum GameLevel {
    EASY,
    NORMAL,
    HARD
}

public class GamePanel extends JPanel implements KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TILE = 64;

    private static final int PLAYER_SPEED = 8;
    private int gameSpeed = 1;
    private final int BASE_SPEED = 1;

    private GameState currentState = GameState.HOME;
    private GameLevel gameLevel = GameLevel.EASY; 

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameOver = false;

    private Random random = new Random();

    private BufferedImage planeImage, bulletImage, heartImage, bossBulletImage;
    private BufferedImage menuImage, homeBackgroundImage, optionBackgroundImage, selectMapBackgroundImage;
    private BufferedImage map1Image, map2Image;
    private BufferedImage[] enemyImages;

    private BufferedImage[] mapBackgrounds;
    private int selectedMap = 0;
    private Boss[] mapBosses;

    private int hearts = 5;

    private Player player;
    private ArrayList<Bullet> bullets = new ArrayList<>(); 
    private ArrayList<Bullet> rainBullets = new ArrayList<>();

    private ArrayList<Enemy> enemy = new ArrayList<>();
    private int enemyCount = 0;

    private Boss boss;
    private boolean readyForBoss = false;
    private boolean allEnemiesCleared = false;
    private long startTime;
    private final long BOSS_SPAWN_TIME = 6000;
    private boolean bossSpawned = false;

    private ArrayList<Explosion> explosions = new ArrayList<>();
    private BufferedImage[] explosionFrames;

    private int playerScore = 0;
    private int moving = 0;

    private Timer gameTimer;

    private double bgY1 = 0;
    private double bgY2 = -HEIGHT;
    private double bgSpeed = 0.5;

    private long lastRainTime = 0;
    private final long RAIN_INTERVAL = 5000; 

    private ButtonManager buttonManager;
    
    public GamePanel() {
        loadFileImages();
        player = new Player();
        boss = new Boss(this); 
        boss.isVisible = false;
        generateEnemies();
        buttonManager = new ButtonManager(this);

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.requestFocusInWindow();

        startGameLoop();
    }    

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(GameLevel level) {
        this.gameLevel = level;
    }


    private void createExplosion(int x, int y) {
        explosions.add(new Explosion(x, y, explosionFrames));
    }

    public void startGameLoop() {
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        gameTimer.start();
    }

    public void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public void paused() {
        if (currentState != GameState.PAUSED) {
            currentState = GameState.PAUSED;
            buttonManager.togglePauseButtons(true);
        } else {
            currentState = GameState.PLAYING;
            buttonManager.togglePauseButtons(false);
        }
    }

    private void update() {
        updateBackground();
        
        if (gameOver || currentState == GameState.PAUSED) return;
        
        if (currentState == GameState.PLAYING) {
            updatePlayer();
            updateBullets();
            updateRainBullets();
            updateEnemies();
            updateBoss();
            updateExplosion();
        }
    }

    private void updateBackground() {
        bgY1 += bgSpeed;
        bgY2 += bgSpeed;

        if (bgY1 >= HEIGHT) {
            bgY1 = bgY2 - HEIGHT;
        }
        if (bgY2 >= HEIGHT) {
            bgY2 = bgY1 - HEIGHT;
        }
    }

    private void updatePlayer() {
        if (moving != 0) {
            int nextX = player.x + moving * PLAYER_SPEED;
            if (nextX < 0) {
                player.x = 0; 
            } else if (nextX > WIDTH - TILE) {
                player.x = WIDTH - TILE; 
            } else {
                player.x = nextX; 
            }
        }
        if (hearts <= 0) {
            currentState = GameState.GAMEOVER;
            gameOver = true;
        }
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet b = iterator.next();
            b.update();
            if (b.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    private void updateRainBullets() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRainTime >= RAIN_INTERVAL) {
            lastRainTime = currentTime;
            int numBullets;
            switch (gameLevel) {
                case EASY:
                    numBullets = 3; 
                    break;
                case NORMAL:
                    numBullets = 5; 
                    break;
                case HARD:
                    numBullets = 7; 
                    break;
                default:
                    numBullets = 3;
            }
            for (int i = 0; i < numBullets; i++) {
                int x = random.nextInt(WIDTH - 15); 
                Bullet bullet = new Bullet(x, 0);
                rainBullets.add(bullet);
            }
        }

        Iterator<Bullet> iterator = rainBullets.iterator();
        while (iterator.hasNext()) {
            Bullet b = iterator.next();
            b.update();
            if (player.getBounds().intersects(b.getBounds())) {
                hearts--;
                iterator.remove();
            } else if (b.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    private void updateEnemies() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime >= BOSS_SPAWN_TIME && !bossSpawned) {
            readyForBoss = true;
            allEnemiesCleared = enemy.isEmpty();
        }

        if (!readyForBoss) {
            enemyCount++;

            if (enemyCount % 60 == 0) {
                int newEnemies = 2 + random.nextInt(2);
                for (int i = 0; i < newEnemies; i++) {
                    generateEnemies();
                }
            }
        }

        if (readyForBoss && enemy.isEmpty() && !allEnemiesCleared) {
            allEnemiesCleared = true;
        }

        Iterator<Enemy> enemyIterator = enemy.iterator();
        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();
            e.y += e.dy;

            Iterator<Bullet> playerBulletIterator = bullets.iterator();
            while (playerBulletIterator.hasNext()) {
                Bullet b = playerBulletIterator.next();
                if (b.isPlayerBullet() && e.getBounds().intersects(b.getBounds())) {
                    playerBulletIterator.remove();
                    if (e.takeDamage()) {
                        enemyIterator.remove();
                        playerScore += 100;
                    }
                    break;
                }
            }

            if (e.getBounds().intersects(player.getBounds())) {
                hearts--;
                enemyIterator.remove();
            }

            if (e.y > HEIGHT) {
                enemyIterator.remove();
                hearts--;
            }
        }
    }

    private void updateBoss() {
        if (!boss.isVisible && readyForBoss && allEnemiesCleared && !gameOver) {
            boss.reset();
            bossSpawned = true;
            boss.isStable = false;
        }

        if (boss.isVisible) {
            boss.move();
            if (boss.isStable) {
                boss.shoot();
            }
            boss.updateBossBullets();

            Iterator<BossBullet> it = boss.getBullets().iterator();
            while (it.hasNext()) {
                BossBullet b = it.next();
                b.move();
                if (player.getBounds().intersects(b.getBounds())) {
                    hearts--;
                    boss.getBullets().remove(b);
                    break;
                }

                if (b.isOffScreen()) {
                    it.remove();
                }
            }

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet b = bulletIterator.next();
                if (b.isPlayerBullet() && boss.isStable && boss.getBounds().intersects(b.getBounds())) {
                    bulletIterator.remove();
                    if (boss.takeDamage()) {
                        createExplosion(boss.x + boss.width / 2, boss.y + boss.height / 2);
                        playerScore += 500;
                        boss.isVisible = false;
                    }
                    break;
                }
            }
        }
    }

    private void updateExplosion() {
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion exp = explosionIterator.next();
            exp.update();
            if (!exp.isActive()) {
                explosionIterator.remove();
                if (explosions.isEmpty()) {
                    currentState = GameState.ENDGAME;
                    gameOver = true;
                }
            }
        }
    }

    private void generateEnemies() {
        Enemy newEnemy = new Enemy(enemyImages, this);
        newEnemy.x = random.nextInt(WIDTH - TILE);
        newEnemy.y = -random.nextInt(HEIGHT / 2);
        newEnemy.dy = gameSpeed + random.nextInt(2) + 1;
        enemy.add(newEnemy);
    }

    public void initGameState() {
        currentState = GameState.PLAYING;
        gameOver = false;
        hearts = 5;
        playerScore = 0;
        moving = 0;
        enemyCount = 0;
        gameSpeed = BASE_SPEED;
        bossSpawned = false;
        startTime = System.currentTimeMillis();
        player = new Player();
        player.x = WIDTH / 2 - TILE / 2;
        player.y = HEIGHT - 90;
        bullets.clear();
        rainBullets.clear();
        enemy.clear();
        for (int i = 0; i < 5; i++) {
            generateEnemies();
        }
        boss = new Boss(mapBosses[selectedMap].image, this); 
        boss.isVisible = false;
        readyForBoss = false;
        allEnemiesCleared = false;
        setLevelParameters();
        buttonManager.toggleEndButtons(false);
    }
    
    public void restartGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        initGameState();
        repaint();
        gameTimer.start();
    }

    private void setLevelParameters() {
        switch (gameLevel) {
            case EASY:
                boss.health = 20;
                break;
            case NORMAL:
                boss.health = 30;
                break;
            case HARD:
                boss.health = 40;
                break;
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadFileImages() {
        homeBackgroundImage = loadImage("/image/homeImg.png");
        optionBackgroundImage = loadImage("/image/map_time4.png");
        selectMapBackgroundImage = loadImage("/image/map_time1.png");
        planeImage = loadImage("/image/spaceship.png");
        bulletImage = loadImage("/image/bullett.png");
        enemyImages = new BufferedImage[] {
            loadImage("/image/enemy1.png"),
            loadImage("/image/enemy2.png"),
            loadImage("/image/enemy3.png"),
            loadImage("/image/enemy4.png")
        };
        bossBulletImage = loadImage("/image/bullet4.png");
        heartImage = loadImage("/image/heart.png");
        menuImage = loadImage("/image/khung.png");
        map1Image = loadImage("/image/map1.png");
        map2Image = loadImage("/image/map2.png");
        mapBackgrounds = new BufferedImage[] {
            loadImage("/image/map_time.png"),
            loadImage("/image/map_time2.png")
        };
        mapBosses = new Boss[] {
            new Boss(loadImage("/image/monster.png"), this),
            new Boss(loadImage("/image/monster1.png"), this)
        };
        explosionFrames = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            explosionFrames[i] = loadImage("/image/explosion" + (i + 1) + ".png");
        }
    }

    private void resetButtonVisibility() {
        buttonManager.toggleMenuButtons(false);
        buttonManager.togglePauseButtons(false);
        buttonManager.toggleEndButtons(false);
        buttonManager.toggleOptionButtons(false);
        buttonManager.toggleMapButtons(false);
        buttonManager.toggleShipSelectionButtons(false);
        buttonManager.toggleLevelSelectionButtons(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        resetButtonVisibility(); 

        switch (currentState) {
            case HOME:
                drawHomeScreen(g);
                buttonManager.toggleMenuButtons(true);
                break;
            case OPTION:
                drawOptionScreen(g);
                buttonManager.toggleOptionButtons(true);
                break;
            case SELECTSS:
                drawOptionScreen(g);
                buttonManager.toggleShipSelectionButtons(true);
                break;
            case SELECTMAP:
                drawMapSelectionScreen(g);
                buttonManager.toggleMapButtons(true);
                break;
            case LEVEL:
                drawLevelSelectionScreen(g);
                buttonManager.toggleLevelSelectionButtons(true);
                break;
            case PLAYING:
                drawGame(g);
                buttonManager.toggleMenuButtons(true);
                break;
            case PAUSED:
                drawGame(g);
                drawPauseMenu(g);
                buttonManager.togglePauseButtons(true);
                break;
            case GAMEOVER:
            case ENDGAME:
                drawGame(g);
                drawGameEndScreen(g);
                buttonManager.toggleEndButtons(true);
                break;
        }
    }

    private Font loadCustomFont(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/Font/Starcraft_Normal.ttf"));
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }

    private void drawHomeScreen(Graphics g) {
        g.drawImage(homeBackgroundImage, 0, 0, WIDTH, HEIGHT, null);
    }

    private void drawGame(Graphics g) {
        g.drawImage(mapBackgrounds[selectedMap], 0, (int) bgY1, WIDTH, HEIGHT, null);
        g.drawImage(mapBackgrounds[selectedMap], 0, (int) bgY2, WIDTH, HEIGHT, null);

        if (!gameOver) {
            g.drawImage(planeImage, player.x, player.y, TILE, TILE, null);
        }
        for (Bullet b : bullets) {
            g.drawImage(bulletImage, b.x - 10, b.y - 28, TILE / 2, TILE / 2, null);
        }
        for (Bullet b : rainBullets) {
            g.drawImage(bulletImage, b.x - 10, b.y - 28, TILE / 2, TILE / 2, null);
        }
        for (Enemy e : enemy) {
            g.drawImage(e.image, e.x, e.y, TILE, TILE, null);
        }
        if (boss != null && boss.isVisible) {
            g.drawImage(boss.image, boss.x, boss.y - 10, boss.width, boss.height, null);
            g.setColor(Color.RED);
            int initialHealth = gameLevel == GameLevel.EASY ? 20 : gameLevel == GameLevel.NORMAL ? 30 : 40;
            g.fillRect(boss.x, boss.y - 20, (int) (boss.width * ((double) boss.health / initialHealth)), 10);
        }
        for (BossBullet b : boss.getBullets()) {
            g.drawImage(bossBulletImage, b.x, b.y - 10, b.Width, b.Height, null);
        }
        for (Explosion exp : explosions) {
            exp.draw(g);
        }
        for (int i = 0; i < hearts; i++) {
            g.drawImage(heartImage, 5 + i * 40, 60, 32, 32, null);
        }
        g.setFont(loadCustomFont(24f));
        g.setColor(Color.WHITE);
        g.drawString("SCORE: " + playerScore, 5, 40);
    }

    private void drawBorder(Graphics g, int x, int y) {
        int menuX = (WIDTH - x) / 2;
        int menuY = (HEIGHT - y) / 2;
        g.drawImage(menuImage, menuX, menuY, x, y, null);
    }

    private void drawMapSelectionScreen(Graphics g) {
        g.drawImage(selectMapBackgroundImage, 0, (int) bgY1, WIDTH, HEIGHT, null);
        g.drawImage(selectMapBackgroundImage, 0, (int) bgY2, WIDTH, HEIGHT, null);

        drawBorder(g, 600, 400);

        g.setColor(Color.WHITE);
        g.setFont(loadCustomFont(36f));
        g.drawString("SELECT MAP", WIDTH / 2 - 135, HEIGHT / 2 - 100);

        g.drawImage(mapBackgrounds[0], WIDTH / 2 - 150, HEIGHT / 2 + 20, 100, 75, null);
        g.drawImage(mapBackgrounds[1], WIDTH / 2 + 50, HEIGHT / 2 + 20, 100, 75, null);
    }

    private void drawOptionScreen(Graphics g) {
        g.drawImage(optionBackgroundImage, 0, (int) bgY1, WIDTH, HEIGHT, null);
        g.drawImage(optionBackgroundImage, 0, (int) bgY2, WIDTH, HEIGHT, null);

        drawBorder(g, 500, 300);

        g.setColor(Color.WHITE);
        g.setFont(loadCustomFont(36f));
        g.drawString("OPTION", WIDTH / 2 - 85, HEIGHT / 2 - 80);
    }

    private void drawLevelSelectionScreen(Graphics g) {
        g.drawImage(optionBackgroundImage, 0, (int) bgY1, WIDTH, HEIGHT, null);
        g.drawImage(optionBackgroundImage, 0, (int) bgY2, WIDTH, HEIGHT, null);

        drawBorder(g, 500, 300);

        g.setColor(Color.WHITE);
        g.setFont(loadCustomFont(36f));
        g.drawString("SELECT LEVEL", WIDTH / 2 - 150, HEIGHT / 2 - 80);
    }

    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawBorder(g, 500, 300);

        g.setColor(Color.WHITE);
        g.setFont(loadCustomFont(36f));
        g.drawString("MENU", WIDTH / 2 - 60, HEIGHT / 2 - 50);
    }

    private void drawGameEndScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawBorder(g, 500, 300);

        if (currentState == GameState.GAMEOVER) {
            g.setColor(Color.RED);
            g.setFont(loadCustomFont(48f));
            g.drawString("GAME OVER!", WIDTH / 2 - 170, HEIGHT / 2 - 50);
        } else if (currentState == GameState.ENDGAME) {
            g.setColor(Color.GREEN);
            g.setFont(loadCustomFont(48f));
            g.drawString("YOU WIN!", WIDTH / 2 - 140, HEIGHT / 2 - 50);
        }

        g.setColor(Color.WHITE);
        g.setFont(loadCustomFont(24f));
        g.drawString("Score: " + playerScore, WIDTH / 2 - 70, HEIGHT / 2);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                moving = -1;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                moving = 1;
                break;
            case KeyEvent.VK_SPACE:
                Bullet newBullet = new Bullet(player);
                newBullet.x = player.x + TILE * 2 / 5;
                newBullet.y = player.y + 20;
                bullets.add(newBullet);
                break;
            case KeyEvent.VK_P:
                paused();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                moving = rightPressed ? 1 : 0;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                moving = leftPressed ? -1 : 0;
                break;
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public void setSelectedMap(int map) {
        this.selectedMap = map;
    }

    public void setPlaneImage(BufferedImage image) {
        this.planeImage = image;
    }
}
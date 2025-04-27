import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Item {
    private static final Random random = new Random();
    public BufferedImage image;
    public int health = 1; 
    public boolean isSpecial = false; 
    private GamePanel gamePanel; 

    public Enemy(BufferedImage[] enemyImages, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.image = enemyImages[random.nextInt(enemyImages.length)];
        if (gamePanel.getGameLevel() != GameLevel.EASY && random.nextInt(10) < 3) {
            isSpecial = true;
            health = 2; 
            this.image = enemyImages[0]; 
        }
    }

    public Enemy(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.x = random.nextInt(GamePanel.WIDTH - 64);  
        this.y = -random.nextInt(200) - 64;
        this.dy = gamePanel.getGameLevel() == GameLevel.HARD ? 2 : 1;
        this.width = 64;
        this.height = 64;
    }

    public boolean takeDamage() {
        health--;
        return health <= 0;
    }
}
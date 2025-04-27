import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Boss {
    public int x, y;
    public boolean isVisible = false;
    public final int width = 270;
    public final int height = 180;
    public final int targetY = 100; 
    public int health = 20; 
    public boolean isStable = false;
    private ArrayList<BossBullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private final long SHOT_DELAY = 4000; 
    private long enterCompleteTime; 
    private static final long DELAY_BEFORE_SHOOT = 1000; 
    public BufferedImage image;
    private GamePanel gamePanel;

    public Boss(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        reset();
    }

    public void reset() {
        this.x = GamePanel.WIDTH / 2 - width/2;
        this.y = -height - 120;
        this.isVisible = true;
        this.health = gamePanel.getGameLevel() == GameLevel.EASY ? 20 : gamePanel.getGameLevel() == GameLevel.NORMAL ? 30 : 40;
        this.bullets.clear();
        this.lastShotTime = 0;
        this.isStable = false; 
    }

    public void move() {
        if (!isStable) {
            if (y < targetY) { 
                y += 2;
            } else {
                isStable = true;
                enterCompleteTime = System.currentTimeMillis();
            }
            return;
        }
    }

    public boolean takeDamage() {
        health--;
        if (health <= 0) {
            isVisible = false;
            return true;
        }
        return false; 
    }

    public void shoot() {
        if (!isStable || System.currentTimeMillis() - enterCompleteTime < DELAY_BEFORE_SHOOT) return;
    
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOT_DELAY) {
            lastShotTime = currentTime;
            int numBullets;
            switch (gamePanel.getGameLevel()) {
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
            int spread = 80; 
            int startX = this.x + this.width/2 - (spread * (numBullets - 1))/2;
            for (int i = 0; i < numBullets; i++) {
                int bulletX = startX + (i * spread);
                int bulletY = this.y + this.height;
                BossBullet bullet = new BossBullet(bulletX, bulletY);
                if (gamePanel.getGameLevel() == GameLevel.HARD) {
                    bullet.dy = 3.5;
                }
                bullets.add(bullet);
            }
        }
    }
    
    public Boss(BufferedImage img, GamePanel gamePanel) {
        this.image = img;
        this.gamePanel = gamePanel;
    }
    
    public ArrayList<BossBullet> getBullets() {
        return bullets;
    }
    
    public void updateBossBullets() {
        Iterator<BossBullet> it = bullets.iterator();
        while (it.hasNext()) {
            BossBullet b = it.next();
            b.move();
            if (b.y < 0 || b.y > GamePanel.HEIGHT) {
                it.remove();
            }
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
// Bullet.java
import java.awt.Rectangle;

public class Bullet extends Item {
    public static final int TILE = 64;

    private Player player;
    private Enemy enemy;
    private boolean isPlayerBullet; 

    public Bullet(Player player) {
        this.player = player;
        this.isPlayerBullet = true;
        this.x = player.x + TILE / 2;
        this.y = player.y;
        this.dy = 15; 
        this.width = 15;
        this.height = 30;
    }

    public Bullet(int x, int y) {
        this.isPlayerBullet = false;
        this.x = x;
        this.y = y;
        this.dy = -5; 
        this.width = 15;
        this.height = 30;
    }

    public void update() {
        if (isPlayerBullet) {
            y -= dy; 
        } else {
            y -= dy; 
        }
    }

    public boolean isOffScreen() {
        if (isPlayerBullet) {
            return y < -64; 
        } else {
            return y > GamePanel.HEIGHT; 
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }
}
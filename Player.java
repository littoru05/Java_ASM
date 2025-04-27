import java.awt.Rectangle;

public class Player extends Item {
    public static final int TILE = 64;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    public Player() {
        this.x = WIDTH/2 - TILE/2;
        this.y = HEIGHT - 90;
        this.dx = 5;
        this.dy = 5;
        this.width = TILE;
        this.height = TILE;
    }

    public void moveLeft() {
        if (x - dx >= 0) x -= dx;
    }

    public void moveRight(int maxWidth) {
        if (x + dx + TILE <= maxWidth) x += dx;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, TILE, TILE);
    }
}
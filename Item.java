
import java.awt.Rectangle;

public class Item {
    public static final int TILE = 64;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    public int x, y, dx, dy, width, height;

    public Item() {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.width = 0;
        this.height = 0;
    }   
    public Rectangle getBounds() {
        return new Rectangle(x, y, TILE, TILE);
    }
}

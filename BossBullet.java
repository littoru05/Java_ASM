import java.awt.Rectangle;

public class BossBullet {
    public int x, y;
    public int Width = 40;
    public int Height = 40;
    public double dy = 2; 
    
    public BossBullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void move() { 
        y += dy;
    }
    
    public boolean isOffScreen() {
        return y > GamePanel.HEIGHT;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x + 10, y + 10, Width - 20, Height - 20); 
    }
}
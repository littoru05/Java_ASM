import java.awt.Graphics;
import java.awt.image.BufferedImage;
public class Explosion {
    private int x, y;
    private int currentFrame = 0;
    private BufferedImage[] frames;
    private boolean active = true;
    private int frameDelay = 5; 
    private int delayCounter = 0;
    
    public Explosion(int x, int y, BufferedImage[] explosionFrames) {
        this.x = x - explosionFrames[0].getWidth()/2; 
        this.y = y - explosionFrames[0].getHeight()/2;
        this.frames = explosionFrames;
    }
    
    public void update() {
        delayCounter++;
        if (delayCounter >= frameDelay) {
            currentFrame++;
            delayCounter = 0;
            if (currentFrame >= frames.length) {
                active = false;
            }
        }
    }
    
    public void draw(Graphics g) {
        if (active && currentFrame < frames.length) {
            g.drawImage(frames[currentFrame], x, y, frames[currentFrame].getWidth(), frames[currentFrame].getHeight(), null);
        }
    }
    
    public boolean isActive() {
        return currentFrame < frames.length;
    }
}
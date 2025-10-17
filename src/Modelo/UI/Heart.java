package Modelo.UI;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Heart {
    
    private int x, y;
    private BufferedImage sprite;
    private boolean visible;
    
    private float oscillationAngle = 0f;
    private float oscillationSpeed = 0.05f;
    private int oscillationAmplitude = 3;
    private int baseY;
    
    public Heart(int x, int y) {
        this.x = x;
        this.baseY = y;
        this.y = y;
        this.visible = true;
        loadSprite();
    }
    
    private void loadSprite() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/res/objects/heart1.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do coração!");
            e.printStackTrace();
        }
    }
    
    public void update() {
        oscillationAngle += oscillationSpeed;
        y = baseY + (int)(Math.sin(oscillationAngle) * oscillationAmplitude);
    }
    
    public void draw(Graphics2D g2) {
        if (visible && sprite != null) {
            g2.drawImage(sprite, x, y, null);
        }
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public int getWidth() {
        return sprite != null ? sprite.getWidth() : 0;
    }
    
    public int getHeight() {
        return sprite != null ? sprite.getHeight() : 0;
    }
}
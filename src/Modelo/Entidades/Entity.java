package Modelo.Entidades;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Entity {

    protected int x, y;
    protected int dx, dy;
    protected int width, height;
    protected int speed;
    protected boolean visible;
    protected BufferedImage sprite;
    protected Rectangle bounds;

    public Entity(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.visible = true;
        this.bounds = new Rectangle();
    }

    public abstract void update();

    public abstract void load();

    public void updateBounds() {
        bounds.setBounds(x, y, width, height);
    }

    public Rectangle getBounds() {
        updateBounds();
        return bounds;
    }

    public boolean intersects(Entity other) {
        return getBounds().intersects(other.getBounds());
    }

    public void draw(Graphics2D g2) {
        if (sprite != null && visible) {
            g2.drawImage(sprite, x, y, null);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpeed() { return speed; }
    public boolean isVisible() { return visible; }
    public BufferedImage getSprite() { return sprite; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
package Modelo.Entidades;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Projectile extends Entity {

    private static final int PROJECTILE_SPEED = 6;
    private int directionX, directionY;
    private Image image;

    public Projectile(int startX, int startY, int directionX, int directionY) {
        super(startX, startY, PROJECTILE_SPEED);
        this.directionX = directionX;
        this.directionY = directionY;
        load();
    }

    @Override
    public void load() {
        try {
            ImageIcon icon = new ImageIcon("src/res/projectile.png");
            image = icon.getImage();
            if (image != null) {
                width = image.getWidth(null);
                height = image.getHeight(null);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do projétil!");
            width = 8;
            height = 8;
        }
    }

    @Override
    public void update() {
        if (!visible) return;

        x += directionX * speed;
        y += directionY * speed;

        if (x < -50 || x > 850 || y < -50 || y > 850) {
            visible = false;
        }
    }

    public Image getImagem() {
        return image;
    }

    public int getLargura() {
        return width;
    }

    public int getAltura() {
        return height;
    }
}
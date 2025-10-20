package Modelo.Entidades;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Projectile extends Entity {

    private static final int PROJECTILE_SPEED = 6;
    private static final int SPRITE_SIZE = 24; // Tamanho do sprite
    private int directionX, directionY;
    private Image image;
    private Image scaledImage;

    public Projectile(int startX, int startY, int directionX, int directionY) {
        // Centraliza o projétil considerando seu tamanho
        super(startX - SPRITE_SIZE / 2, startY - SPRITE_SIZE / 2, PROJECTILE_SPEED);
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
                // Escala a imagem para o tamanho reduzido
                scaledImage = image.getScaledInstance(SPRITE_SIZE, SPRITE_SIZE, Image.SCALE_SMOOTH);
                width = SPRITE_SIZE;
                height = SPRITE_SIZE;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do projétil!");
            width = SPRITE_SIZE;
            height = SPRITE_SIZE;
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

    public void draw(Graphics2D g2) {
        if (visible && scaledImage != null) {
            g2.drawImage(scaledImage, x, y, null);
        }
    }

    public Image getImagem() {
        return scaledImage != null ? scaledImage : image;
    }

    public int getLargura() {
        return width;
    }

    public int getAltura() {
        return height;
    }
}
package Modelo.Entidades;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Projectile extends Entity {

    private static final int DEFAULT_SPEED = 4;
    private static final int DEFAULT_SIZE = 24;
    
    private int directionX, directionY;
    private Image image;
    private Image scaledImage;
    private int spriteSize;

    private Projectile(ProjectileBuilder builder) {
        super(
            builder.startX - builder.spriteSize / 2, 
            builder.startY - builder.spriteSize / 2, 
            builder.speed
        );
        this.directionX = builder.directionX;
        this.directionY = builder.directionY;
        this.spriteSize = builder.spriteSize;
        load();
    }

    @Override
    public void load() {
        try {
            ImageIcon icon = new ImageIcon("src/res/projectile.png");
            image = icon.getImage();
            if (image != null) {
                scaledImage = image.getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH);
                width = spriteSize;
                height = spriteSize;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do projétil!");
            width = spriteSize;
            height = spriteSize;
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

    @Override
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

    // ========================================
    // BUILDER PATTERN
    // ========================================
    
    public static class ProjectileBuilder {
        private final int startX;
        private final int startY;
        private final int directionX;
        private final int directionY;
        
        private int speed = DEFAULT_SPEED;
        private int spriteSize = DEFAULT_SIZE;

        public ProjectileBuilder(int startX, int startY, int directionX, int directionY) {
            this.startX = startX;
            this.startY = startY;
            this.directionX = directionX;
            this.directionY = directionY;
        }

        public ProjectileBuilder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public ProjectileBuilder spriteSize(int spriteSize) {
            this.spriteSize = spriteSize;
            return this;
        }

        public Projectile build() {
            return new Projectile(this);
        }
    }
}
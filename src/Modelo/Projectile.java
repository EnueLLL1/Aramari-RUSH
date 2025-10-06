package Modelo;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Projectile {
    private int x, y;
    private int dx, dy;
    private Image imagem;
    private int largura, altura;
    private boolean visible;

    public Projectile(int startX, int startY, int directionX, int directionY) {
        this.x = startX;
        this.y = startY;
        this.dx = directionX;
        this.dy = directionY; 
        this.visible = true;
        load();
    }

    private void load() {
        ImageIcon icon = new ImageIcon("src/res/projectile.png");
        imagem = icon.getImage();
        if (imagem != null) {
            largura = imagem.getWidth(null);
            altura = imagem.getHeight(null);
        }
    }

    public void update() {
        x += dx;
        y += dy;
        if (x > 800) {
            visible = false;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImagem() {
        return imagem;
    }

    public boolean isVisible() {
        return visible;
    }
}

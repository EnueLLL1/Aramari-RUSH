package Modelo;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Enemy {
    private int x, y;
    private int dx, dy;
    private Image imagem;
    private int largura, altura;
    private boolean visivel;
    private EnemyType type;
    private int health;
    private int damage;
    private int speed;
    private int score;

    public enum EnemyType {
        TIPO1,
        TIPO2
    }

    // Construtor privado - só pode ser criado pelo Builder
    private Enemy(EnemyBuilder builder) {
        this.x = builder.x;
        this.y = builder.y;
        this.type = builder.type;
        this.health = builder.health;
        this.damage = builder.damage;
        this.speed = builder.speed;
        this.score = builder.score;
        this.visivel = true;
        this.dx = 0;
        this.dy = 0;
        load();
    }

    private void load() {
        String path = "";
        switch (type) {
            case TIPO1:
                path = "src/res/personagem/phrEnemy1.png";
                break;
            case TIPO2:
                path = "src/res/personagem/phrEnemy2.png";
                break;
        }

        ImageIcon referencia = new ImageIcon(path);
        imagem = referencia.getImage();
        if (imagem != null) {
            this.largura = imagem.getWidth(null);
            this.altura = imagem.getHeight(null);
        }
    }

    public void update(int playerX, int playerY) {
        if (!visivel) return;

        // Movimento em direção ao jogador
        int deltaX = playerX - x;
        int deltaY = playerY - y;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            dx = (int) ((deltaX / distance) * speed);
            dy = (int) ((deltaY / distance) * speed);

            x += dx;
            y += dy;
        }
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            visivel = false;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, largura, altura);
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public Image getImagem() { return imagem; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
    public boolean isVisivel() { return visivel; }
    public EnemyType getType() { return type; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getSpeed() { return speed; }
    public int getScore() { return score; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVisivel(boolean visivel) { this.visivel = visivel; }

    // Builder Pattern
    public static class EnemyBuilder {
        // Parâmetros obrigatórios
        private final int x;
        private final int y;
        private final EnemyType type;

        // Parâmetros opcionais com valores padrão
        private int health = 100;
        private int damage = 10;
        private int speed = 1;
        private int score = 50;

        public EnemyBuilder(int x, int y, EnemyType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public EnemyBuilder health(int health) {
            this.health = health;
            return this;
        }

        public EnemyBuilder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public EnemyBuilder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public EnemyBuilder score(int score) {
            this.score = score;
            return this;
        }

        public Enemy build() {
            return new Enemy(this);
        }
    }
}
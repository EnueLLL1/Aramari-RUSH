package Modelo.Entidades;

import java.awt.Graphics2D;
import java.awt.Color;
import javax.imageio.ImageIO;

public class Enemy extends Entity {

    private EnemyType type;
    private int health;
    private int maxHealth;
    private int damage;
    private int score;

    public enum EnemyType {
        TIPO1,
        TIPO2
    }

    private Enemy(EnemyBuilder builder) {
        super(builder.x, builder.y, builder.speed);
        this.type = builder.type;
        this.health = builder.health;
        this.maxHealth = builder.health;
        this.damage = builder.damage;
        this.score = builder.score;
        load();
    }

    @Override
    public void load() {
        String path = "";
        switch (type) {
            case TIPO1:
                path = "/res/personagem/phrEnemy1.png";
                break;
            case TIPO2:
                path = "/res/personagem/phrEnemy2.png";
                break;
        }

        try {
            sprite = ImageIO.read(getClass().getResourceAsStream(path));
            width = sprite.getWidth();
            height = sprite.getHeight();
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do inimigo: " + path);
            width = 32;
            height = 32;
        }
    }

    @Override
    public void update() {
        if (!visible) return;
    }

    public void update(int targetX, int targetY) {
        if (!visible) return;

        int deltaX = targetX - x;
        int deltaY = targetY - y;

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
            health = 0;
            visible = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        if (visible) {
            g2.setColor(Color.RED);
            g2.fillRect(x, y - 5, width, 3);
            g2.setColor(Color.GREEN);
            int healthWidth = (int) (width * ((float) health / maxHealth));
            g2.fillRect(x, y - 5, healthWidth, 3);
        }
    }

    public EnemyType getType() { return type; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getScore() { return score; }

    public boolean isVisivel() { return visible; }
    public void setVisivel(boolean visible) { this.visible = visible; }

    public int getLargura() { return width; }
    public int getAltura() { return height; }
    public java.awt.image.BufferedImage getImagem() { return sprite; }

    public static class EnemyBuilder {
        private final int x;
        private final int y;
        private final EnemyType type;

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
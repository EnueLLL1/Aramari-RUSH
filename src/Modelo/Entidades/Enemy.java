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

    // Posições em ponto flutuante para movimento suave
    private double preciseX;
    private double preciseY;

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
        this.preciseX = builder.x;
        this.preciseY = builder.y;
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

    /**
     * Atualiza a posição do inimigo em direção ao alvo
     * Retorna true se conseguiu se mover, false se ficou parado
     */
    public boolean update(int targetX, int targetY) {
        if (!visible) return false;

        double deltaX = targetX - preciseX;
        double deltaY = targetY - preciseY;

        double distanceSquared = deltaX * deltaX + deltaY * deltaY;

        // Se já está muito próximo do alvo, não precisa se mover
        if (distanceSquared < 0.1) {
            dx = 0;
            dy = 0;
            return false;
        }

        double distance = Math.sqrt(distanceSquared);

        // Normaliza o vetor de direção e multiplica pela velocidade
        double dirX = (deltaX / distance) * speed;
        double dirY = (deltaY / distance) * speed;

        // Atualiza posição com precisão
        preciseX += dirX;
        preciseY += dirY;

        // Atualiza posições inteiras para renderização
        int newX = (int) Math.round(preciseX);
        int newY = (int) Math.round(preciseY);

        // Calcula dx e dy para detecção de colisão
        dx = newX - x;
        dy = newY - y;

        x = newX;
        y = newY;

        return true;
    }

    /**
     * Reverte o último movimento quando houver colisão
     */
    public void revertMovement() {
        // Volta para a posição anterior
        x -= dx;
        y -= dy;

        // Sincroniza as posições precisas
        preciseX = x;
        preciseY = y;

        // Zera o movimento
        dx = 0;
        dy = 0;
    }

    /**
     * Tenta mover apenas no eixo X em direção ao alvo
     */
    public void tryMoveX(int targetX) {
        if (!visible) return;

        double deltaX = targetX - preciseX;

        if (Math.abs(deltaX) < 0.1) {
            dx = 0;
            return;
        }

        // Normaliza apenas o eixo X
        double dirX = (deltaX / Math.abs(deltaX)) * speed;

        int oldX = x;
        preciseX += dirX;
        x = (int) Math.round(preciseX);
        dx = x - oldX;
        dy = 0;
    }

    /**
     * Tenta mover apenas no eixo Y em direção ao alvo
     */
    public void tryMoveY(int targetY) {
        if (!visible) return;

        double deltaY = targetY - preciseY;

        if (Math.abs(deltaY) < 0.1) {
            dy = 0;
            return;
        }

        // Normaliza apenas o eixo Y
        double dirY = (deltaY / Math.abs(deltaY)) * speed;

        int oldY = y;
        preciseY += dirY;
        y = (int) Math.round(preciseY);
        dy = y - oldY;
        dx = 0;
    }

    /**
     * Força sincronização das posições precisas com as inteiras
     */
    public void syncPrecisePosition() {
        preciseX = x;
        preciseY = y;
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
            // Desenha barra de vida
            int barWidth = width;
            int barHeight = 3;
            int barX = x;
            int barY = y - 5;

            // Fundo vermelho
            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            // Vida verde
            g2.setColor(Color.GREEN);
            int healthWidth = (int) (barWidth * ((float) health / maxHealth));
            g2.fillRect(barX, barY, healthWidth, barHeight);
        }
    }

    // Getters
    public EnemyType getType() { return type; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getScore() { return score; }
    public boolean isVisivel() { return visible; }
    public int getLargura() { return width; }
    public int getAltura() { return height; }
    public java.awt.image.BufferedImage getImagem() { return sprite; }

    // Setters
    public void setVisivel(boolean visible) { this.visible = visible; }

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
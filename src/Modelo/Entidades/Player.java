package Modelo.Entidades;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Entity {

    private int pdx, pdy;
    private int lastPdx = 1, lastPdy = 0;
    private String direction;
    private boolean moving;

    private boolean upPressed, downPressed, leftPressed, rightPressed;

    private boolean canMoveUp = true;
    private boolean canMoveDown = true;
    private boolean canMoveLeft = true;
    private boolean canMoveRight = true;

    private BufferedImage up1, up2, up3, up4;
    private BufferedImage down1, down2, down3, down4;
    private BufferedImage left1, left2, left3, left4;
    private BufferedImage right1, right2, right3, right4;

    private int spriteCounter = 0;
    private int spriteNum = 1;
    private final int SPRITE_ANIMATION_SPEED = 10;

    // Sistema de vidas
    private int health;
    private int maxHealth;
    private boolean invulnerable = false;
    private int invulnerabilityTimer = 0;
    private final int INVULNERABILITY_DURATION = 30;

    private Player(PlayerBuilder builder) {
        super(builder.x, builder.y, builder.speed);
        this.health = builder.health;
        this.maxHealth = builder.maxHealth;
        this.direction = "down";
        this.moving = false;
        load();
    }

    @Override
    public void load() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_up_2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_up_3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_up_4.png"));

            down1 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_down_2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_down_3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_down_4.png"));

            left1 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_left_2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_left_3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_left_4.png"));

            right1 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_right_2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_right_3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("/res/personagem/player_right_4.png"));

            sprite = down1;
            width = sprite.getWidth();
            height = sprite.getHeight();
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprites do player!");
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        moving = false;
        dx = 0;
        dy = 0;

        if (upPressed && canMoveUp) {
            dy = -speed;
            direction = "up";
            moving = true;
            lastPdy = -1;
            lastPdx = 0;
        }
        if (downPressed && canMoveDown) {
            dy = speed;
            direction = "down";
            moving = true;
            lastPdy = 1;
            lastPdx = 0;
        }
        if (leftPressed && canMoveLeft) {
            dx = -speed;
            direction = "left";
            moving = true;
            lastPdx = -1;
            lastPdy = 0;
        }
        if (rightPressed && canMoveRight) {
            dx = speed;
            direction = "right";
            moving = true;
            lastPdx = 1;
            lastPdy = 0;
        }

        x += dx;
        y += dy;

        if (dx != 0 || dy != 0) {
            pdx = dx;
            pdy = dy;
        }

        if (!upPressed) canMoveUp = true;
        if (!downPressed) canMoveDown = true;
        if (!leftPressed) canMoveLeft = true;
        if (!rightPressed) canMoveRight = true;

        if (invulnerable) {
            invulnerabilityTimer--;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
            }
        }

        updateAnimation();
    }

    private void updateAnimation() {
        if (moving) {
            spriteCounter++;
            if (spriteCounter > SPRITE_ANIMATION_SPEED) {
                spriteNum++;
                if (spriteNum > 4) spriteNum = 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
            spriteCounter = 0;
        }

        switch (direction) {
            case "up":
                sprite = switch (spriteNum) {
                    case 1 -> up1;
                    case 2 -> up2;
                    case 3 -> up3;
                    case 4 -> up4;
                    default -> up1;
                };
                break;
            case "down":
                sprite = switch (spriteNum) {
                    case 1 -> down1;
                    case 2 -> down2;
                    case 3 -> down3;
                    case 4 -> down4;
                    default -> down1;
                };
                break;
            case "left":
                sprite = switch (spriteNum) {
                    case 1 -> left1;
                    case 2 -> left2;
                    case 3 -> left3;
                    case 4 -> left4;
                    default -> left1;
                };
                break;
            case "right":
                sprite = switch (spriteNum) {
                    case 1 -> right1;
                    case 2 -> right2;
                    case 3 -> right3;
                    case 4 -> right4;
                    default -> right1;
                };
                break;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (sprite != null && visible) {
            if (!invulnerable || (invulnerabilityTimer / 5) % 2 == 0) {
                g2.drawImage(sprite, x, y, null);
            }
        }
    }

    public void takeDamage(int damage) {
        if (!invulnerable && health > 0) {
            health -= damage;
            if (health < 0) health = 0;
            
            invulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public void resetHealth() {
        health = maxHealth;
        invulnerable = false;
        invulnerabilityTimer = 0;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    public void keyRelease(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    public void stopMovement() {
        dx = 0;
        dy = 0;

        if (direction.equals("up")) canMoveUp = false;
        else if (direction.equals("down")) canMoveDown = false;
        else if (direction.equals("left")) canMoveLeft = false;
        else if (direction.equals("right")) canMoveRight = false;
    }

    public void stopVerticalMovement() {
        dy = 0;
        if (direction.equals("up")) canMoveUp = false;
        else if (direction.equals("down")) canMoveDown = false;
    }

    public void stopHorizontalMovement() {
        dx = 0;
        if (direction.equals("left")) canMoveLeft = false;
        else if (direction.equals("right")) canMoveRight = false;
    }

    public void enableAllMovement() {
        canMoveUp = true;
        canMoveDown = true;
        canMoveLeft = true;
        canMoveRight = true;
    }

    // Getters
    public int getPdx() { return pdx; }
    public int getPdy() { return pdy; }
    public int getLastPdx() { return lastPdx; }
    public int getLastPdy() { return lastPdy; }
    public String getDirection() { return direction; }
    public boolean isMoving() { return moving; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isInvulnerable() { return invulnerable; }
    public int getLargura() { return width; }
    public int getAltura() { return height; }
    public BufferedImage getImagem() { return sprite; }

    // Setters
    public void setPdx(int pdx) { this.pdx = pdx; }
    public void setPdy(int pdy) { this.pdy = pdy; }

    // Builder Pattern
    public static class PlayerBuilder {
        private final int x;
        private final int y;
        
        private int speed = 3;
        private int health = 3;
        private int maxHealth = 3;

        public PlayerBuilder(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public PlayerBuilder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public PlayerBuilder health(int health) {
            this.health = health;
            this.maxHealth = health;
            return this;
        }

        public PlayerBuilder maxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
package Modelo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import AramariRUSH.Container;
import Modelo.Audio.SoundManager;
import Modelo.Entidades.Collectible;
import Modelo.Entidades.Enemy;
import Modelo.Entidades.EnemyFactory;
import Modelo.Entidades.Player;
import Modelo.Entidades.PowerUp;
import Modelo.Entidades.Projectile;
import Modelo.UI.GameOverScreen;
import Modelo.UI.Heart;
import Modelo.UI.ScreenShake;
import Modelo.UI.WinScreen;
import tile.TileManager;

public class GameplayPanel extends JPanel implements ActionListener {

    // Timers
    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer spawnTimer;
    private Timer enemySpawnTimer;
    private Timer powerUpSpawnTimer;

    // Game state
    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isWin = false;
    private boolean isStarted = false;
    private int score = 0;

    // Power-up state e constantes
    private boolean tripleShotActive = false;
    private long tripleShotEndTime = 0;
    private static final int POWER_UP_SPAWN_INTERVAL = 15000;

    // Bonus mode state
    private boolean bonusModeActive = false;
    private static final int BONUS_MODE_THRESHOLD = 30;
    private static final int NORMAL_ENEMY_SPAWN_DELAY = 3000;
    private static final int BONUS_ENEMY_SPAWN_DELAY = 1000;

    // FPS tracking
    private long lastFpsTime = System.nanoTime();
    private int fpsCounter = 0;
    private int currentFps = 0;

    // Screen constants
    public final int tileSize = 32;
    public final int maxScreenCol = 25;
    public final int maxScreenRow = 25;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // Managers
    private final TileManager tileM;
    private final ScreenShake screenShake;
    private final Random rand;
    private final SoundManager soundManager;

    // Game objects
    private Player player;
    private final ArrayList<Enemy> enemies;
    private final ArrayList<Projectile> projectiles;
    private final ArrayList<Collectible> collectibles;
    private final ArrayList<PowerUp> powerUps;
    private final ArrayList<Heart> hearts;

    // Strategy & UI
    private ScoreStrategy scoreStrategy;
    private final GameOverScreen gameOverScreen;
    private final WinScreen winScreen;
    private final Container containerRef;

    // Debug
    private boolean debugMode = false;

    public GameplayPanel(Container container) {
        this.containerRef = container;

        tileM = new TileManager(this);
        screenShake = new ScreenShake();
        rand = new Random();
        soundManager = SoundManager.getInstance();
        
        player = new Player.PlayerBuilder(400, 400)
                .speed(3)
                .health(3)
                .build();
        
        projectiles = new ArrayList<>();
        collectibles = new ArrayList<>();
        powerUps = new ArrayList<>();
        enemies = new ArrayList<>();
        hearts = new ArrayList<>();
        scoreStrategy = new CommonScoreStrategy();
        gameOverScreen = new GameOverScreen(container, this);
        winScreen = new WinScreen(container, this);

        setupPanel();
        setupTimers();
        initializeHearts();
        
        addMouseListener(new MouseInputAdapter());
    }

    private void setupPanel() {
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setLayout(null);
        addKeyListener(new TecladoAdapter());
    }

    private void setupTimers() {
        gameTimer = new Timer(16, this);
        gameTimer.setCoalesce(true);
        gameTimer.setInitialDelay(0);

        countdownTimer = new Timer(1000, e -> updateTime());
        spawnTimer = new Timer(3000, e -> spawnDiamond());
        enemySpawnTimer = new Timer(NORMAL_ENEMY_SPAWN_DELAY, e -> spawnEnemy());
        powerUpSpawnTimer = new Timer(10000, e -> trySpawnPowerUp());

        gameTimer.start();
        countdownTimer.start();
        spawnTimer.start();
        enemySpawnTimer.start();
        powerUpSpawnTimer.start();
        
        soundManager.playMusic();
    }

    private void initializeHearts() {
        hearts.clear();
        int heartSpacing = 40;
        int heartStartX = (screenWidth >> 1) - (heartSpacing * player.getMaxHealth() >> 1);
        int heartY = 20;

        for (int i = 0; i < player.getMaxHealth(); i++) {
            hearts.add(new Heart(heartStartX + (i * heartSpacing), heartY));
        }
    }

    private void updateHearts() {
        int playerHealth = player.getHealth();
        for (int i = 0, size = hearts.size(); i < size; i++) {
            Heart heart = hearts.get(i);
            heart.update();
            heart.setVisible(i < playerHealth);
        }
    }

    public void reiniciarJogo() {
        isGameOver = false;
        isWin = false;
        isStarted = true;
        score = 0;
        timeLeft = 120;
        tripleShotActive = false;
        tripleShotEndTime = 0;
        bonusModeActive = false;

        scoreStrategy = new CommonScoreStrategy();

        projectiles.clear();
        collectibles.clear();
        enemies.clear();
        powerUps.clear();

        player = new Player.PlayerBuilder(400, 400)
                .speed(3)
                .health(3)
                .build();

        initializeHearts();

        gameOverScreen.hide();
        winScreen.hide();

        restartAllTimers();
        
        soundManager.playMusic();
        
        requestFocusInWindow();
    }

    private void voltarAoMenu() {
        stopAllTimers();

        gameOverScreen.hide();
        winScreen.hide();

        isGameOver = false;
        isWin = false;
        isStarted = false;
        score = 0;
        timeLeft = 120;
        tripleShotActive = false;
        tripleShotEndTime = 0;
        bonusModeActive = false;

        scoreStrategy = new CommonScoreStrategy();

        projectiles.clear();
        collectibles.clear();
        enemies.clear();
        powerUps.clear();

        containerRef.showScreen("Menu");
    }

    private void trySpawnPowerUp() {
        if (!isStarted || isGameOver || isWin) return;

        if (rand.nextInt(100) < 40) {
            spawnPowerUp();
        }
    }

    private void spawnPowerUp() {
        int x = 0, y = 0;
        int maxAttempts = 20;
        int attempts = 0;
        boolean validPosition = false;

        while (attempts < maxAttempts && !validPosition) {
            x = (rand.nextInt(maxScreenCol - 2) + 1) * tileSize;
            y = (rand.nextInt(maxScreenRow - 2) + 1) * tileSize;
            validPosition = !checkTileCollisionAt(x, y, 32, 32);
            attempts++;
        }

        if (validPosition) {
            powerUps.add(new PowerUp.PowerUpBuilder(x, y, PowerUp.PowerUpType.TRIPLE_SHOT).build());
        }
    }

    private void spawnDiamond() {
        if (!isStarted || isGameOver || isWin) return;

        int x = 0, y = 0;
        int maxAttempts = 20;
        int attempts = 0;
        boolean validPosition = false;

        while (attempts < maxAttempts && !validPosition) {
            x = (rand.nextInt(maxScreenCol - 2) + 1) * tileSize;
            y = (rand.nextInt(maxScreenRow - 2) + 1) * tileSize;
            validPosition = !checkTileCollisionAt(x, y, 32, 32);
            attempts++;
        }

        int chance = rand.nextInt(100);
        Collectible.DiamondType type = chance < 60 ? Collectible.DiamondType.COMUM :
                chance < 90 ? Collectible.DiamondType.RARO :
                        Collectible.DiamondType.LENDARIO;

        collectibles.add(new Collectible.CollectibleBuilder(x, y, type).build());
    }

    private void spawnEnemy() {
        if (!isStarted || isGameOver) return;

        int side = rand.nextInt(4);
        int screenCenterX = getWidth() >> 1;
        int screenCenterY = getHeight() >> 1;
        int spawnMargin = getHeight() / 6;
        int randomOffset = rand.nextInt(spawnMargin << 1) - spawnMargin;

        int x, y;
        switch (side) {
            case 0:
                x = -32;
                y = screenCenterY + randomOffset;
                break;
            case 1:
                x = screenWidth - 32;
                y = screenCenterY + randomOffset;
                break;
            case 2:
                x = screenCenterX + randomOffset;
                y = -32;
                break;
            default:
                x = screenCenterX + randomOffset;
                y = screenHeight - 32;
                break;
        }

        enemies.add(EnemyFactory.createRandomEnemy(x, y));
    }

    private void checkCollisions() {
        Iterator<Collectible> it = collectibles.iterator();
        while (it.hasNext()) {
            Collectible c = it.next();
            if (c.isVisible() && player.intersects(c)) {
                score += scoreStrategy.calculateScore(c);
                c.setVisible(false);
                soundManager.playSound("collect"); 
                it.remove();
            }
        }
    
        Iterator<PowerUp> powerUpIt = powerUps.iterator();
        while (powerUpIt.hasNext()) {
            PowerUp powerUp = powerUpIt.next();
            if (powerUp.isVisible() && player.intersects(powerUp)) {
                activatePowerUp(powerUp);
                powerUp.setVisible(false);
                soundManager.playSound("collect");
                powerUpIt.remove();
            }
        }
    }

    private void activatePowerUp(PowerUp powerUp) {
        if (powerUp.getType() == PowerUp.PowerUpType.TRIPLE_SHOT) {
            tripleShotActive = true;
            tripleShotEndTime = System.currentTimeMillis() + powerUp.getDuration();
        }
    }

    private void updatePowerUps() {
        if (tripleShotActive && System.currentTimeMillis() >= tripleShotEndTime) {
            tripleShotActive = false;
        }
    }

    private void checkProjectileEnemyCollisions() {
        Iterator<Projectile> projIt = projectiles.iterator();

        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            Iterator<Enemy> enemyIt = enemies.iterator();
            boolean hit = false;

            while (enemyIt.hasNext() && !hit) {
                Enemy enemy = enemyIt.next();

                if (enemy.isVisible() && proj.intersects(enemy)) {
                    enemy.takeDamage(20);
                    projIt.remove();
                    hit = true;

                    if (!enemy.isVisible()) {
                        score += enemy.getScore();
                        enemyIt.remove();
                    }
                }
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible() && player.intersects(enemy)) {
                player.takeDamage(1);
                screenShake.startShake(5, 15);
                soundManager.playSound("damage");
                if (player.getHealth() <= 0) {
                    endGameAsLoss();
                }
                break;
            }
        }
    }

    private boolean checkPlayerTileCollision() {
        int hitboxMargin = 3;
        int hitboxX = player.getX() + hitboxMargin;
        int hitboxY = player.getY() + hitboxMargin;
        int hitboxWidth = player.getWidth() - (hitboxMargin << 1);
        int hitboxHeight = player.getHeight() - (hitboxMargin << 1);

        int leftCol = hitboxX / tileSize;
        int rightCol = (hitboxX + hitboxWidth) / tileSize;
        int topRow = hitboxY / tileSize;
        int bottomRow = (hitboxY + hitboxHeight) / tileSize;

        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        leftCol = Math.max(0, leftCol);
        rightCol = Math.min(maxScreenCol - 1, rightCol);
        topRow = Math.max(0, topRow);
        bottomRow = Math.min(maxScreenRow - 1, bottomRow);

        return tileM.tile[tileM.mapTileNum[leftCol][topRow]].collision ||
                tileM.tile[tileM.mapTileNum[rightCol][topRow]].collision ||
                tileM.tile[tileM.mapTileNum[leftCol][bottomRow]].collision ||
                tileM.tile[tileM.mapTileNum[rightCol][bottomRow]].collision;
    }

    private boolean checkProjectileTileCollision(Projectile p) {
        return checkTileCollisionAt(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    }

    private boolean checkTileCollisionAt(int x, int y, int width, int height) {
        int leftCol = x / tileSize;
        int rightCol = (x + width) / tileSize;
        int topRow = y / tileSize;
        int bottomRow = (y + height) / tileSize;

        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                if (tileM.tile[tileM.mapTileNum[col][row]].collision) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateTime() {
        if (!isStarted) return;

        if (timeLeft > 0) {
            timeLeft--;
            
            if (timeLeft == BONUS_MODE_THRESHOLD && !bonusModeActive) {
                activateBonusMode();
            }
            
            return;
        }

        endGameAsWin();
    }

    private void activateBonusMode() {
        bonusModeActive = true;
        
        scoreStrategy = new BonusScoreStrategy();
        
        enemySpawnTimer.stop();
        enemySpawnTimer = new Timer(BONUS_ENEMY_SPAWN_DELAY, e -> spawnEnemy());
        enemySpawnTimer.start();
        
        System.out.println("🔥 MODO BONUS ATIVADO! 🔥");
        System.out.println("Pontuação dobrada e mais inimigos!");
    }

    private void endGameAsWin() {
        isWin = true;
        isStarted = false;
        stopAllTimers();
        soundManager.stopMusic();
        soundManager.playSound("win");
        winScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
    }

    private void endGameAsLoss() {
        isGameOver = true;
        isStarted = false;
        stopAllTimers();
        soundManager.stopMusic();
        soundManager.playSound("gameover");
        gameOverScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
    }

    private void stopAllTimers() {
        countdownTimer.stop();
        gameTimer.stop();
        spawnTimer.stop();
        enemySpawnTimer.stop();
        powerUpSpawnTimer.stop();
    }

    private void restartAllTimers() {
        gameTimer.restart();
        countdownTimer.restart();
        spawnTimer.restart();
        
        enemySpawnTimer.stop();
        enemySpawnTimer = new Timer(NORMAL_ENEMY_SPAWN_DELAY, e -> spawnEnemy());
        enemySpawnTimer.start();
        
        powerUpSpawnTimer.restart();
    }

    private void updateFPS() {
        fpsCounter++;
        long currentTime = System.nanoTime();

        if (currentTime - lastFpsTime >= 1_000_000_000L) {
            currentFps = fpsCounter;
            fpsCounter = 0;
            lastFpsTime = currentTime;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.translate(screenShake.getOffsetX(), screenShake.getOffsetY());

        tileM.draw(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        player.draw(g2);
        drawProjectiles(g2);
        drawCollectibles(g2);
        drawPowerUps(g2);
        drawEnemies(g2);

        g2.translate(-screenShake.getOffsetX(), -screenShake.getOffsetY());

        drawHearts(g2);

        if (isGameOver) {
            gameOverScreen.draw(g2, getWidth(), getHeight());
            return;
        }

        if (isWin) {
            winScreen.draw(g2, getWidth(), getHeight());
            return;
        }

        drawHUD(g2);

        if (debugMode) {
            drawDebugInfo(g2);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawProjectiles(Graphics2D g2) {
        for (Projectile p : projectiles) {
            if (p.getImagem() != null) {
                g2.drawImage(p.getImagem(), p.getX(), p.getY(), this);
            }
        }
    }

    private void drawCollectibles(Graphics2D g2) {
        for (Collectible c : collectibles) {
            if (c.isVisible() && c.getSprite() != null) {
                g2.drawImage(c.getSprite(), c.getX(), c.getY(), this);
            }
        }
    }

    private void drawPowerUps(Graphics2D g2) {
        for (PowerUp p : powerUps) {
            if (p.isVisible() && p.getSprite() != null) {
                g2.drawImage(p.getSprite(), p.getX(), p.getY(), this);
            }
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            enemy.draw(g2);
        }
    }

    private void drawHearts(Graphics2D g2) {
        for (Heart heart : hearts) {
            heart.draw(g2);
        }
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Pontuação: " + score, 20, 30);
        
        if (bonusModeActive) {
            g2.setColor(Color.RED);
            g2.drawString("Tempo: " + formatTime(timeLeft) + " BONUS!", 20, 55);
        } else {
            g2.setColor(Color.WHITE);
            g2.drawString("Tempo: " + formatTime(timeLeft), 20, 55);
        }
        
        if (tripleShotActive) {
           long timeRemaining = (tripleShotEndTime - System.currentTimeMillis()) / 1000;
           g2.setColor(Color.CYAN);
           g2.setFont(new Font("Arial", Font.BOLD, 18));
           g2.drawString("TIRO TRIPLO: " + timeRemaining + "s", 20, 80);
        }

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("FPS: " + currentFps, getWidth() - 70, 20);
    }

    private void drawDebugInfo(Graphics2D g2) {
        g2.setColor(new Color(255, 0, 0, 160));
        g2.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        g2.setColor(new Color(0, 255, 0, 160));
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g2.drawRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            }
        }

        g2.setColor(new Color(0, 0, 255, 60));
        for (int col = 0; col < maxScreenCol; col++) {
            for (int row = 0; row < maxScreenRow; row++) {
                if (tileM.tile[tileM.mapTileNum[col][row]].collision) {
                    g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }

        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Vida: " + player.getHealth() + "/" + player.getMaxHealth(), 20, 105);
        if (player.isInvulnerable()) {
            g2.drawString("INVULNERÁVEL", 20, 120);
        }

        g2.setColor(new Color(255, 0, 255, 80));
        int spawnMargin = getHeight() / 6;
        int screenCenterX = getWidth() >> 1;
        int screenCenterY = getHeight() >> 1;

        g2.fillRect(-32, screenCenterY - spawnMargin, 64, spawnMargin * 2);
        g2.fillRect(screenWidth - 32, screenCenterY - spawnMargin, 64, spawnMargin * 2);
        g2.fillRect(screenCenterX - spawnMargin, -32, spawnMargin * 2, 64);
        g2.fillRect(screenCenterX - spawnMargin, screenHeight - 32, spawnMargin * 2, 64);

        if (tripleShotActive) {
            g2.drawString("POWER-UP ATIVO", 20, 135);
        }
        
        if (bonusModeActive) {
            g2.setColor(Color.RED);
            g2.drawString("MODO BONUS ATIVO!", 20, 150);
            g2.drawString("Estratégia: BonusScoreStrategy", 20, 165);
            g2.drawString("Enemy Spawn: " + BONUS_ENEMY_SPAWN_DELAY + "ms", 20, 180);
        } else {
            g2.setColor(Color.GREEN);
            g2.drawString("Estratégia: CommonScoreStrategy", 20, 150);
            g2.drawString("Enemy Spawn: " + NORMAL_ENEMY_SPAWN_DELAY + "ms", 20, 165);
        }

        g2.setColor(Color.YELLOW);
        g2.drawString("DEBUG MODE ON (F3)", 20, getHeight() - 20);
        g2.drawString("Áreas de Spawn ON", 20, getHeight() - 35);
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted && !isGameOver && !isWin) {
            updatePlayer();
            updateProjectiles();
            updateEnemies();
            updatePowerUps();

            checkCollisions();
            checkProjectileEnemyCollisions();
            checkPlayerEnemyCollisions();
        }

        screenShake.update();
        updateHearts();
        updateFPS();
        repaint();
    }

    private void updatePlayer() {
        int oldX = player.getX();
        int oldY = player.getY();

        player.update();

        player.setX(Math.max(0, Math.min(player.getX(), screenWidth - player.getWidth())));
        player.setY(Math.max(0, Math.min(player.getY(), screenHeight - player.getHeight())));

        if (checkPlayerTileCollision()) {
            player.setX(oldX);
            player.setY(oldY);
            player.stopMovement();
        }
    }

    private void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();

            if (!p.isVisible() || checkProjectileTileCollision(p)) {
                it.remove();
            }
        }
    }

    private void updateEnemies() {
        int playerX = player.getX();
        int playerY = player.getY();

        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) continue;

            int oldX = enemy.getX();
            int oldY = enemy.getY();

            enemy.update(playerX, playerY);

            if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                    enemy.getWidth(), enemy.getHeight())) {

                enemy.setX(oldX);
                enemy.setY(oldY);
                enemy.syncPrecisePosition();

                enemy.tryMoveX(playerX);

                if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                        enemy.getWidth(), enemy.getHeight())) {

                    enemy.setX(oldX);
                    enemy.syncPrecisePosition();
                    enemy.tryMoveY(playerY);

                    if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                            enemy.getWidth(), enemy.getHeight())) {
                        enemy.setY(oldY);
                        enemy.syncPrecisePosition();
                    }
                }
            }
        }
    }

    private class TecladoAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_F3) {
                debugMode = !debugMode;
                System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));
                return;
            }

            player.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e);
        }
    }

    private class MouseInputAdapter extends java.awt.event.MouseAdapter {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (!isStarted || isGameOver || isWin) return;
            fireProjectile(e.getX(), e.getY());
        }
    }

    private void fireProjectile(int mouseX, int mouseY) {
        int playerCenterX = player.getX() + (player.getWidth() >> 1);
        int playerCenterY = player.getY() + (player.getHeight() >> 1);

        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;

        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = dx / length;
            dy = dy / length;
        }

        int projectileSpeed = 4;
        dx *= projectileSpeed;
        dy *= projectileSpeed;

        if (tripleShotActive) {
            projectiles.add(new Projectile.ProjectileBuilder(
                    playerCenterX, 
                    playerCenterY, 
                    (int)dx, 
                    (int)dy
            ).build());

            double angle = -Math.PI / 12;
            double leftDx = dx * Math.cos(angle) - dy * Math.sin(angle);
            double leftDy = dx * Math.sin(angle) + dy * Math.cos(angle);
            projectiles.add(new Projectile.ProjectileBuilder(
                    playerCenterX, 
                    playerCenterY, 
                    (int)leftDx, 
                    (int)leftDy
            ).build());

            angle = Math.PI / 12;
            double rightDx = dx * Math.cos(angle) - dy * Math.sin(angle);
            double rightDy = dx * Math.sin(angle) + dy * Math.cos(angle);
            projectiles.add(new Projectile.ProjectileBuilder(
                    playerCenterX, 
                    playerCenterY, 
                    (int)rightDx, 
                    (int)rightDy
            ).build());
        } else {
            projectiles.add(new Projectile.ProjectileBuilder(
                    playerCenterX,
                    playerCenterY,
                    (int)dx,
                    (int)dy
            ).build());
        }

        soundManager.playSound("shoot");
    }

    public boolean isStarted() { return isStarted; }
    public void setStarted(boolean started) { this.isStarted = started; }
}
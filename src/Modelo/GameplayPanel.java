package Modelo;

import AramariRUSH.Container;
import Modelo.Entidades.*;
import Modelo.UI.GameOverScreen;
import Modelo.UI.Heart;
import Modelo.UI.ScreenShake;
import Modelo.UI.WinScreen;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;
import tile.TileManager;

public class GameplayPanel extends JPanel implements ActionListener {

    // Timers
    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer spawnTimer;
    private Timer enemySpawnTimer;

    // Game state
    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isWin = false;
    private boolean isStarted = false;
    private int score = 0;

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

    // Game objects
    private final Player player;
    private final ArrayList<Enemy> enemies;
    private final ArrayList<Projectile> projectiles;
    private final ArrayList<Collectible> collectibles;
    private final ArrayList<Heart> hearts;

    // Strategy & UI
    private final ScoreStrategy scoreStrategy;
    private final GameOverScreen gameOverScreen;
    private final WinScreen winScreen;
    private final Container containerRef;

    // Debug
    private boolean debugMode = false;

    public GameplayPanel(Container container) {
        this.containerRef = container;

        // Inicializa objetos finais
        tileM = new TileManager(this);
        screenShake = new ScreenShake();
        rand = new Random();
        player = new Player(400, 400, 3);
        projectiles = new ArrayList<>();
        collectibles = new ArrayList<>();
        enemies = new ArrayList<>();
        hearts = new ArrayList<>();
        scoreStrategy = new CommonScoreStrategy();
        gameOverScreen = new GameOverScreen(container, this);
        winScreen = new WinScreen(container, this);

        setupPanel();
        setupTimers();
        initializeHearts();
        
        // Adiciona o listener do mouse
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
        enemySpawnTimer = new Timer(3000, e -> spawnEnemy());

        gameTimer.start();
        countdownTimer.start();
        spawnTimer.start();
        enemySpawnTimer.start();
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
        // Reset game state
        isGameOver = false;
        isWin = false;
        isStarted = true;
        score = 0;
        timeLeft = 120;

        // Clear collections
        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        // Reset player
        player.setX(400);
        player.setY(400);
        player.enableAllMovement();
        player.resetHealth();

        initializeHearts();

        // Hide screens
        gameOverScreen.hide();
        winScreen.hide();

        // Restart timers
        restartAllTimers();
        requestFocusInWindow();
    }

    private void voltarAoMenu() {
        stopAllTimers();

        gameOverScreen.hide();
        winScreen.hide();

        // Reset state
        isGameOver = false;
        isWin = false;
        isStarted = false;
        score = 0;
        timeLeft = 120;

        // Clear collections
        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        containerRef.showScreen("Menu");
    }

    private void spawnDiamond() {
        if (!isStarted || isGameOver || isWin) return;

        int x = 0, y = 0;
        int maxAttempts = 20;
        int attempts = 0;
        boolean validPosition = false;

        // Procura posição válida
        while (attempts < maxAttempts && !validPosition) {
            x = (rand.nextInt(maxScreenCol - 2) + 1) * tileSize;
            y = (rand.nextInt(maxScreenRow - 2) + 1) * tileSize;
            validPosition = !checkTileCollisionAt(x, y, 32, 32);
            attempts++;
        }

        // Determina tipo baseado em probabilidade
        int chance = rand.nextInt(100);
        Collectible.DiamondType type = chance < 60 ? Collectible.DiamondType.COMUM :
                chance < 90 ? Collectible.DiamondType.RARO :
                        Collectible.DiamondType.LENDARIO;

        collectibles.add(new Collectible(x, y, type));
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
            case 0: // Esquerda
                x = -32; // Move para fora da tela
                y = screenCenterY + randomOffset;
                break;
            case 1: // Direita
                x = screenWidth - 32; // Alinha com a área de debug
                y = screenCenterY + randomOffset;
                break;
            case 2: // Topo
                x = screenCenterX + randomOffset;
                y = -32;
                break;
            default: // Base
                x = screenCenterX + randomOffset;
                y = screenHeight - 32; // Alinha com a área de debug
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
                it.remove();
            }
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

        // Bounds check
        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        // Clamp to valid range
        leftCol = Math.max(0, leftCol);
        rightCol = Math.min(maxScreenCol - 1, rightCol);
        topRow = Math.max(0, topRow);
        bottomRow = Math.min(maxScreenRow - 1, bottomRow);

        // Check corners only
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

        // Bounds check
        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        // Check all tiles in area
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
            return;
        }

        // Tempo acabou - vitória!
        endGameAsWin();
    }

    private void endGameAsWin() {
        isWin = true;
        isStarted = false;
        stopAllTimers();
        winScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
    }

    private void endGameAsLoss() {
        isGameOver = true;
        isStarted = false;
        stopAllTimers();
        gameOverScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
    }

    private void stopAllTimers() {
        countdownTimer.stop();
        gameTimer.stop();
        spawnTimer.stop();
        enemySpawnTimer.stop();
    }

    private void restartAllTimers() {
        gameTimer.restart();
        countdownTimer.restart();
        spawnTimer.restart();
        enemySpawnTimer.restart();
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

        // Aplica screen shake
        g2.translate(screenShake.getOffsetX(), screenShake.getOffsetY());

        // Desenha cenário
        tileM.draw(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha entidades
        player.draw(g2);
        drawProjectiles(g2);
        drawCollectibles(g2);
        drawEnemies(g2);

        // Remove translação para UI
        g2.translate(-screenShake.getOffsetX(), -screenShake.getOffsetY());

        // Desenha UI
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
            if (c.isVisible() && c.getImagem() != null) {
                g2.drawImage(c.getImagem(), c.getX(), c.getY(), this);
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
        g2.drawString("Tempo: " + formatTime(timeLeft), 20, 55);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("FPS: " + currentFps, getWidth() - 70, 20);
    }

    private void drawDebugInfo(Graphics2D g2) {
        // Player hitbox
        g2.setColor(new Color(255, 0, 0, 160));
        g2.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        // Enemy hitboxes
        g2.setColor(new Color(0, 255, 0, 160));
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g2.drawRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            }
        }

        // Collision tiles
        g2.setColor(new Color(0, 0, 255, 60));
        for (int col = 0; col < maxScreenCol; col++) {
            for (int row = 0; row < maxScreenRow; row++) {
                if (tileM.tile[tileM.mapTileNum[col][row]].collision) {
                    g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }

        // Player info
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Vida: " + player.getHealth() + "/" + player.getMaxHealth(), 20, 80);
        if (player.isInvulnerable()) {
            g2.drawString("INVULNERÁVEL", 20, 95);
        }

        // Desenha áreas de spawn de inimigos
        g2.setColor(new Color(255, 0, 255, 80)); // Cor roxa semi-transparente
        int spawnMargin = getHeight() / 6;
        int screenCenterX = getWidth() >> 1;
        int screenCenterY = getHeight() >> 1;

        // Área de spawn esquerda
        g2.fillRect(-32, screenCenterY - spawnMargin, 64, spawnMargin * 2);
        
        // Área de spawn direita
        g2.fillRect(screenWidth - 32, screenCenterY - spawnMargin, 64, spawnMargin * 2);
        
        // Área de spawn superior
        g2.fillRect(screenCenterX - spawnMargin, -32, spawnMargin * 2, 64);
        
        // Área de spawn inferior
        g2.fillRect(screenCenterX - spawnMargin, screenHeight - 32, spawnMargin * 2, 64);

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

        // Clamp to screen bounds
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

                // Reverte e sincroniza
                enemy.setX(oldX);
                enemy.setY(oldY);
                enemy.syncPrecisePosition();

                // Tenta X
                enemy.tryMoveX(playerX);

                if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                        enemy.getWidth(), enemy.getHeight())) {

                    // Reverte X, tenta Y
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
            if (!isStarted || isGameOver) return;
            fireProjectile(e.getX(), e.getY());
        }
    }

    private void fireProjectile(int mouseX, int mouseY) {
        // Calcula o centro do jogador
        int playerCenterX = player.getX() + (player.getWidth() >> 1);
        int playerCenterY = player.getY() + (player.getHeight() >> 1);

        // Calcula o vetor direção
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;

        // Normaliza o vetor
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = dx / length;
            dy = dy / length;
        }

        // Multiplica pela velocidade desejada
        int projectileSpeed = 8;
        dx *= projectileSpeed;
        dy *= projectileSpeed;

        projectiles.add(new Projectile(playerCenterX, playerCenterY, (int)dx, (int)dy));
    }
    

    public boolean isStarted() { return isStarted; }
    public void setStarted(boolean started) { this.isStarted = started; }
}
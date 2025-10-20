package Modelo;

import AramariRUSH.Container;
import Modelo.Entidades.*;
import Modelo.UI.GameOverScreen;
import Modelo.UI.Heart;
import Modelo.UI.ScreenShake;
import Modelo.UI.WinScreen;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameplayPanel extends JPanel implements ActionListener {

    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer spawnTimer;
    private Timer enemySpawnTimer;

    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isWin = false;
    private boolean isStarted = false;

    private long lastFpsTime = System.nanoTime();
    private int fpsCounter = 0;
    private int currentFps = 0;

    public final int tileSize = 32;
    public final int maxScreenCol = 25;
    public final int maxScreenRow = 25;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    TileManager tileM = new TileManager(this);

    private int score = 0;

    private boolean debugMode = false;

    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Collectible> collectibles;
    private ArrayList<Heart> hearts;

    private ScreenShake screenShake;

    private Random rand = new Random();
    private Player player;

    private ScoreStrategy scoreStrategy;

    private GameOverScreen gameOverScreen;
    private WinScreen winScreen;
    private Container containerRef;

    public GameplayPanel(Container container) {
        this.containerRef = container;

        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setLayout(null);

        gameTimer = new Timer(16, this);
        gameTimer.setCoalesce(true);
        gameTimer.setInitialDelay(0);

        countdownTimer = new Timer(1000, e -> updateTime());
        spawnTimer = new Timer(3000, e -> spawnDiamond());
        enemySpawnTimer = new Timer(3000, e -> spawnEnemy());

        player = new Player(400, 400, 3);
        projectiles = new ArrayList<>();
        collectibles = new ArrayList<>();
        enemies = new ArrayList<>();
        hearts = new ArrayList<>();
        
        screenShake = new ScreenShake();
        
        initializeHearts();

        scoreStrategy = new CommonScoreStrategy();

        addKeyListener(new TecladoAdapter());

        gameTimer.start();
        countdownTimer.start();
        spawnTimer.start();
        enemySpawnTimer.start();

        gameOverScreen = new GameOverScreen(container, this);
        winScreen = new WinScreen(container, this);
    }

    private void initializeHearts() {
        hearts.clear();
        int heartSpacing = 40;
        int heartStartX = (screenWidth / 2) - (heartSpacing * player.getMaxHealth() / 2);
        int heartY = 20;

        for (int i = 0; i < player.getMaxHealth(); i++) {
            Heart heart = new Heart(heartStartX + (i * heartSpacing), heartY);
            hearts.add(heart);
        }
    }

    private void updateHearts() {
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).update();
            hearts.get(i).setVisible(i < player.getHealth());
        }
    }

    public void reiniciarJogo() {
        isGameOver = false;
        isWin = false;
        isStarted = true;
        score = 0;
        timeLeft = 120;

        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        player.setX(400);
        player.setY(400);
        player.enableAllMovement();
        player.resetHealth();
        
        initializeHearts();

        gameOverScreen.hide();
        winScreen.hide();

        gameTimer.restart();
        countdownTimer.restart();
        spawnTimer.restart();
        enemySpawnTimer.restart();

        requestFocusInWindow();
    }

    private void voltarAoMenu() {
        gameTimer.stop();
        countdownTimer.stop();
        spawnTimer.stop();
        enemySpawnTimer.stop();

        gameOverScreen.hide();
        winScreen.hide();

        isGameOver = false;
        isWin = false;
        isStarted = false;
        score = 0;
        timeLeft = 120;

        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        containerRef.showScreen("Menu");
    }

    private void spawnDiamond() {
        if (!isStarted || isGameOver || isWin) return;

        int x, y;
        int maxAttempts = 20;
        int attempts = 0;

        do {
            x = rand.nextInt(maxScreenCol - 2) + 1;
            y = rand.nextInt(maxScreenRow - 2) + 1;
            x *= tileSize;
            y *= tileSize;
            attempts++;
        } while (attempts < maxAttempts && checkTileCollisionAt(x, y, 32, 32));

        int chance = rand.nextInt(100);
        Collectible.DiamondType type;

        if (chance < 60) {
            type = Collectible.DiamondType.COMUM;
        } else if (chance < 90) {
            type = Collectible.DiamondType.RARO;
        } else {
            type = Collectible.DiamondType.LENDARIO;
        }

        collectibles.add(new Collectible(x, y, type));
    }

    private void spawnEnemy() {
        if (!isStarted || isGameOver || isWin) return;

        int x, y;
        int maxAttempts = 20;
        int attempts = 0;

        do {
            x = rand.nextInt(maxScreenCol - 2) + 1;
            y = rand.nextInt(maxScreenRow - 2) + 1;
            x *= tileSize;
            y *= tileSize;
            attempts++;
        } while (attempts < maxAttempts && checkTileCollisionAt(x, y, 32, 32));

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
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();

                if (enemy.isVisible() && proj.intersects(enemy)) {
                    //Dano de 20
                    enemy.takeDamage(20);
                    projIt.remove();

                    if (!enemy.isVisible()) {
                        score += enemy.getScore();
                        enemyIt.remove();
                    }

                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible() && player.intersects(enemy)) {
                player.takeDamage(1);
                
                // Inicia o screen shake
                screenShake.startShake(5, 15);
                
                if (player.getHealth() <= 0) {
                    isGameOver = true;
                    setStarted(false);
                    countdownTimer.stop();
                    gameTimer.stop();
                    enemySpawnTimer.stop();
                    spawnTimer.stop();

                    gameOverScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
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

        if (leftCol < 0) leftCol = 0;
        if (rightCol >= maxScreenCol) rightCol = maxScreenCol - 1;
        if (topRow < 0) topRow = 0;
        if (bottomRow >= maxScreenRow) bottomRow = maxScreenRow - 1;

        int tileNum1 = tileM.mapTileNum[leftCol][topRow];
        int tileNum2 = tileM.mapTileNum[rightCol][topRow];
        int tileNum3 = tileM.mapTileNum[leftCol][bottomRow];
        int tileNum4 = tileM.mapTileNum[rightCol][bottomRow];

        return tileM.tile[tileNum1].collision ||
                tileM.tile[tileNum2].collision ||
                tileM.tile[tileNum3].collision ||
                tileM.tile[tileNum4].collision;
    }

    private boolean checkProjectileTileCollision(Projectile p) {
        int leftCol = p.getX() / tileSize;
        int rightCol = (p.getX() + p.getWidth()) / tileSize;
        int topRow = p.getY() / tileSize;
        int bottomRow = (p.getY() + p.getHeight()) / tileSize;

        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                if (col >= 0 && col < maxScreenCol && row >= 0 && row < maxScreenRow) {
                    int tileNum = tileM.mapTileNum[col][row];
                    if (tileM.tile[tileNum].collision) {
                        return true;
                    }
                }
            }
        }

        return false;
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
                int tileNum = tileM.mapTileNum[col][row];
                if (tileM.tile[tileNum].collision) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateFPS() {
        fpsCounter++;
        long currentTime = System.nanoTime();

        if (currentTime - lastFpsTime >= 1000000000L) {
            currentFps = fpsCounter;
            fpsCounter = 0;
            lastFpsTime = currentTime;
        }
    }

    private void drawFPS(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("FPS: " + currentFps, getWidth() - 70, 20);
    }

    private void updateTime() {
        if (this.isStarted) {
            if (timeLeft > 0) {
                timeLeft--;
            } else {
                // Tempo acabou - jogador venceu!
                isWin = true;
                setStarted(false);
                countdownTimer.stop();
                gameTimer.stop();
                spawnTimer.stop();
                enemySpawnTimer.stop();

                winScreen.show(score, this::reiniciarJogo, this::voltarAoMenu);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graficos = (Graphics2D) g;
        
        // Aplica o screen shake
        graficos.translate(screenShake.getOffsetX(), screenShake.getOffsetY());

        tileM.draw(graficos);

        graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        player.draw(graficos);

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            if (projectile.getImagem() != null) {
                graficos.drawImage(projectile.getImagem(), projectile.getX(), projectile.getY(), this);
            }
        }

        for (int i = 0; i < collectibles.size(); i++) {
            Collectible collectible = collectibles.get(i);
            if (collectible.isVisible() && collectible.getImagem() != null) {
                graficos.drawImage(collectible.getImagem(), collectible.getX(), collectible.getY(), this);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(graficos);
        }
        
        // Remove a translação do shake antes de desenhar UI
        graficos.translate(-screenShake.getOffsetX(), -screenShake.getOffsetY());

        // Desenha os corações
        for (Heart heart : hearts) {
            heart.draw(graficos);
        }

        if (isGameOver) {
            gameOverScreen.draw(graficos, getWidth(), getHeight());
            return;
        }
        
        if (isWin) {
            winScreen.draw(graficos, getWidth(), getHeight());
            return;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Pontuação: " + score, 20, 30);
        g.drawString("Tempo: " + formatTime(timeLeft), 20, 55);
        drawFPS(graficos);

        if (debugMode) {
            // Player - vermelho
            g.setColor(new Color(255, 0, 0, 160));
            g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

            // Inimigos - verde
            g.setColor(new Color(0, 255, 0, 160));
            for (Enemy enemy : enemies) {
                if (enemy.isVisible()) {
                    g.drawRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                }
            }

            // Tiles sólidos - azul translúcido
            g.setColor(new Color(0, 0, 255, 60));
            for (int col = 0; col < maxScreenCol; col++) {
                for (int row = 0; row < maxScreenRow; row++) {
                    int tileNum = tileM.mapTileNum[col][row];
                    if (tileM.tile[tileNum].collision) {
                        int drawX = col * tileSize;
                        int drawY = row * tileSize;
                        g.fillRect(drawX, drawY, tileSize, tileSize);
                    }
                }
            }

            // Info de vida
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Vida: " + player.getHealth() + "/" + player.getMaxHealth(), 20, 80);
            if (player.isInvulnerable()) {
                g.drawString("INVULNERÁVEL", 20, 95);
            }

            // Texto no canto
            g.setColor(Color.YELLOW);
            g.drawString("DEBUG MODE ON (F3)", 20, getHeight() - 20);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (isStarted && !isGameOver && !isWin) {

            int oldX = player.getX();
            int oldY = player.getY();

            player.update();

            // Impede o jogador de sair da tela
            if (player.getX() < 0) player.setX(0);
            if (player.getY() < 0) player.setY(0);

            if (player.getX() + player.getWidth() > screenWidth)
                player.setX(screenWidth - player.getWidth());
            if (player.getY() + player.getHeight() > screenHeight)
                player.setY(screenHeight - player.getHeight());

            if (checkPlayerTileCollision()) {
                player.setX(oldX);
                player.setY(oldY);
                player.stopMovement();
            }

            Iterator<Projectile> it = projectiles.iterator();
            while (it.hasNext()) {
                Projectile p = it.next();
                p.update();

                if (checkProjectileTileCollision(p)) {
                    it.remove();
                    continue;
                }
                if (!p.isVisible()) {
                    it.remove();
                }
            }

            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (enemy.isVisible()) {
                    int oldEnemyX = enemy.getX();
                    int oldEnemyY = enemy.getY();

                    enemy.update(player.getX(), player.getY());

                    if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                            enemy.getWidth(), enemy.getHeight())) {
                        enemy.setX(oldEnemyX);
                        enemy.setY(oldEnemyY);
                    }
                }
            }

            checkCollisions();
            checkProjectileEnemyCollisions();
            checkPlayerEnemyCollisions();
        }
        
        // Atualiza o screen shake
        screenShake.update();
        
        // Atualiza os corações
        updateHearts();
        
        updateFPS();
        repaint();
    }

    private class TecladoAdapter extends KeyAdapter {

        boolean apertinho = false;

        @Override
        public void keyPressed(KeyEvent e) {

            int codigo = e.getKeyCode();

            if (codigo == KeyEvent.VK_F3) {
                debugMode = !debugMode;
                System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));
                return;
            }

            if (codigo == KeyEvent.VK_SPACE) {
                int projDx = player.getPdx() != 0 ? player.getPdx() : player.getLastPdx();
                int projDy = player.getPdy() != 0 ? player.getPdy() : player.getLastPdy();

                if (projDx == 0 && projDy == 0) {
                    projDx = player.getLastPdx();
                    projDy = player.getLastPdy();
                }
                player.setPdx(0);
                player.setPdy(0);
                if (!apertinho) {
                    apertinho = true;
                    projectiles.add(new Projectile(
                            player.getX() + (player.getWidth() / 2),
                            player.getY() + (player.getHeight() / 2),
                            projDx,
                            projDy
                    ));
                }
            } else {
                player.keyPressed(e);
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e);
            apertinho = false;
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

}
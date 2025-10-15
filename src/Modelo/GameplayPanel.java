package Modelo;

import AramariRUSH.Container;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;
import tile.TileManager;

public class GameplayPanel extends JPanel implements ActionListener {

    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer spawnTimer;
    private Timer enemySpawnTimer;

    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isStarted = false;

    private long lastFpsTime = System.nanoTime();
    private int fpsCounter = 0;
    private int currentFps = 0;

    public final int tileSize = 16;
    public final int maxScreenCol = 50;
    public final int maxScreenRow = 50;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    TileManager tileM = new TileManager(this);

    private int score = 0;

    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Collectible> collectibles;

    private Random rand = new Random();
    private Player player;

    private ScoreStrategy scoreStrategy;

    private JButton btnJogarNovamente;
    private JButton btnVoltarMenu;
    private Container containerRef;

    private Rectangle playerBounds;
    private Rectangle projBounds;

    public GameplayPanel(Container container) {
        this.containerRef = container;

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setLayout(null);

        gameTimer = new Timer(16, this);
        gameTimer.setCoalesce(true);
        gameTimer.setInitialDelay(0);

        countdownTimer = new Timer(1000, e -> updateTime());
        spawnTimer = new Timer(3000, e -> spawnDiamond());
        enemySpawnTimer = new Timer(5000, e -> spawnEnemy());

        player = new Player();
        player.load();
        projectiles = new ArrayList<>();
        collectibles = new ArrayList<>();
        enemies = new ArrayList<>();

        playerBounds = new Rectangle();
        projBounds = new Rectangle();

        scoreStrategy = new CommonScoreStrategy();

        addKeyListener(new TecladoAdapter());

        gameTimer.start();
        countdownTimer.start();
        spawnTimer.start();
        enemySpawnTimer.start();

        criarBotoesGameOver();
    }

    private void criarBotoesGameOver() {
        btnJogarNovamente = new JButton("Jogar Novamente");
        btnJogarNovamente.setFont(new Font("Arial", Font.BOLD, 20));
        btnJogarNovamente.setFocusable(false);
        btnJogarNovamente.setVisible(false);

        btnJogarNovamente.setBackground(new Color(0, 200, 0));
        btnJogarNovamente.setForeground(Color.WHITE);
        btnJogarNovamente.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btnJogarNovamente.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnJogarNovamente.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnJogarNovamente.setBackground(new Color(0, 255, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnJogarNovamente.setBackground(new Color(0, 200, 0));
            }
        });

        btnJogarNovamente.addActionListener(e -> reiniciarJogo());

        btnVoltarMenu = new JButton("Voltar ao Menu");
        btnVoltarMenu.setFont(new Font("Arial", Font.BOLD, 20));
        btnVoltarMenu.setFocusable(false);
        btnVoltarMenu.setVisible(false);

        btnVoltarMenu.setBackground(new Color(200, 0, 0));
        btnVoltarMenu.setForeground(Color.WHITE);
        btnVoltarMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btnVoltarMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVoltarMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVoltarMenu.setBackground(new Color(255, 0, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnVoltarMenu.setBackground(new Color(200, 0, 0));
            }
        });

        btnVoltarMenu.addActionListener(e -> voltarAoMenu());

        add(btnJogarNovamente);
        add(btnVoltarMenu);
    }

    private void posicionarBotoesGameOver() {
        int btnWidth = 250;
        int btnHeight = 60;
        int centerX = getWidth() / 2 - btnWidth / 2;
        int centerY = getHeight() / 2 + 80;
        int spacing = 20;

        btnJogarNovamente.setBounds(centerX, centerY, btnWidth, btnHeight);
        btnVoltarMenu.setBounds(centerX, centerY + btnHeight + spacing, btnWidth, btnHeight);
    }

    public void reiniciarJogo() {
        isGameOver = false;
        isStarted = true;
        score = 0;
        timeLeft = 120;

        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        player.setX(400);
        player.setY(400);

        btnJogarNovamente.setVisible(false);
        btnVoltarMenu.setVisible(false);

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

        btnJogarNovamente.setVisible(false);
        btnVoltarMenu.setVisible(false);

        isGameOver = false;
        isStarted = false;
        score = 0;
        timeLeft = 120;

        projectiles.clear();
        collectibles.clear();
        enemies.clear();

        containerRef.showScreen("Menu");
    }

    private void spawnDiamond() {
        if (!isStarted || isGameOver) {
            return;
        }

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
        if (!isStarted || isGameOver) {
            return;
        }

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
        playerBounds.setBounds(player.getX(), player.getY(), player.getLargura(), player.getAltura());

        Iterator<Collectible> it = collectibles.iterator();
        while (it.hasNext()) {
            Collectible c = it.next();
            if (c.isVisivel() && playerBounds.intersects(c.getBounds())) {
                score += scoreStrategy.calculateScore(c);
                c.setVisivel(false);
                it.remove();
            }
        }
    }

    private void checkProjectileEnemyCollisions() {
        Iterator<Projectile> projIt = projectiles.iterator();

        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            projBounds.setBounds(proj.getX(), proj.getY(), proj.getLargura(), proj.getAltura());

            Iterator<Enemy> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();

                if (enemy.isVisivel() && projBounds.intersects(enemy.getBounds())) {
                    enemy.takeDamage(50);
                    projIt.remove();

                    if (!enemy.isVisivel()) {
                        score += enemy.getScore();
                        enemyIt.remove();
                    }

                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        playerBounds.setBounds(player.getX(), player.getY(), player.getLargura(), player.getAltura());

        for (Enemy enemy : enemies) {
            if (enemy.isVisivel() && playerBounds.intersects(enemy.getBounds())) {
                isGameOver = true;
                setStarted(false);
                countdownTimer.stop();
                gameTimer.stop();
                enemySpawnTimer.stop();

                btnJogarNovamente.setVisible(true);
                btnVoltarMenu.setVisible(true);
                posicionarBotoesGameOver();

                break;
            }
        }
    }

    private boolean checkPlayerTileCollision() {
        int hitboxMargin = 3;
        int hitboxX = player.getX() + hitboxMargin;
        int hitboxY = player.getY() + hitboxMargin;
        int hitboxWidth = player.getLargura() - (hitboxMargin << 1);
        int hitboxHeight = player.getAltura() - (hitboxMargin << 1);

        int leftCol = hitboxX / tileSize;
        int rightCol = (hitboxX + hitboxWidth) / tileSize;
        int topRow = hitboxY / tileSize;
        int bottomRow = (hitboxY + hitboxHeight) / tileSize;

        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

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
        int rightCol = (p.getX() + p.getLargura()) / tileSize;
        int topRow = p.getY() / tileSize;
        int bottomRow = (p.getY() + p.getAltura()) / tileSize;

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
                isGameOver = true;
                setStarted(false);
                countdownTimer.stop();
                gameTimer.stop();

                btnJogarNovamente.setVisible(true);
                btnVoltarMenu.setVisible(true);
                posicionarBotoesGameOver();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graficos = (Graphics2D) g;

        tileM.draw(graficos);

        graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (player.getImagem() != null) {
            graficos.drawImage(player.getImagem(), player.getX(), player.getY(), this);
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            if (projectile.getImagem() != null) {
                graficos.drawImage(projectile.getImagem(), projectile.getX(), projectile.getY(), this);
            }
        }

        for (int i = 0; i < collectibles.size(); i++) {
            Collectible collectible = collectibles.get(i);
            if (collectible.isVisivel() && collectible.getImagem() != null) {
                graficos.drawImage(collectible.getImagem(), collectible.getX(), collectible.getY(), this);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isVisivel() && enemy.getImagem() != null) {
                graficos.drawImage(enemy.getImagem(), enemy.getX(), enemy.getY(), this);

                graficos.setColor(Color.RED);
                graficos.fillRect(enemy.getX(), enemy.getY() - 5, enemy.getLargura(), 3);
                graficos.setColor(Color.GREEN);
                int healthWidth = (int) (enemy.getLargura() * (enemy.getHealth() / 100.0));
                graficos.fillRect(enemy.getX(), enemy.getY() - 5, healthWidth, 3);
            }
        }

        if (isGameOver) {
            graficos.setColor(Color.RED);
            graficos.setFont(new Font("Arial", Font.BOLD, 64));
            String gameOverText = "GAME OVER";
            int textWidth = graficos.getFontMetrics().stringWidth(gameOverText);
            graficos.drawString(gameOverText, getWidth() / 2 - textWidth / 2, getHeight() / 2 - 50);

            graficos.setColor(Color.WHITE);
            graficos.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreText = "Pontuação: " + score;
            int scoreWidth = graficos.getFontMetrics().stringWidth(scoreText);
            graficos.drawString(scoreText, getWidth() / 2 - scoreWidth / 2, getHeight() / 2 + 10);

            return;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Pontuação: " + score, 20, 30);
        g.drawString("Tempo: " + formatTime(timeLeft), 20, 55);
        drawFPS(graficos);

        Toolkit.getDefaultToolkit().sync();
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (isStarted && !isGameOver) {

            int oldX = player.getX();
            int oldY = player.getY();

            player.update();

            if (checkPlayerTileCollision()) {
                player.setX(oldX);
                player.setY(oldY);
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
                if (enemy.isVisivel()) {
                    int oldEnemyX = enemy.getX();
                    int oldEnemyY = enemy.getY();

                    enemy.update(player.getX(), player.getY());

                    if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                            enemy.getLargura(), enemy.getAltura())) {
                        enemy.setX(oldEnemyX);
                        enemy.setY(oldEnemyY);
                    }
                }
            }

            checkCollisions();
            checkProjectileEnemyCollisions();
            checkPlayerEnemyCollisions();
        }
        updateFPS();
        repaint();
    }

    private class TecladoAdapter extends KeyAdapter {

        boolean apertinho = false;

        @Override
        public void keyPressed(KeyEvent e) {

            int codigo = e.getKeyCode();
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
                            player.getX() + (player.getLargura() / 2),
                            player.getY() + (player.getAltura() / 2),
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
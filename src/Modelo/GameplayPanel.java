package Modelo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import tile.TileManager;

public class GameplayPanel extends JPanel implements ActionListener {

    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer spawnTimer;
    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isStarted = false;


    //Todos os tamanhos em pixels 
    //TODO ajustar depois
    
    public final int tileSize = 16; // 16x16 pixels
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
    private Timer timer;
    private Timer enemySpawnTimer;

    // Estratégia de pontuação (Strategy Pattern) 
    private ScoreStrategy scoreStrategy;

    public GameplayPanel() {
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        //Timer do jogo
        gameTimer = new Timer(16, this);
        countdownTimer = new Timer(1000, e -> updateTime());
        spawnTimer = new Timer(3000, e -> spawnDiamond());
        enemySpawnTimer = new Timer(5000, e -> spawnEnemy()); // Spawn a cada 5 segundos

        player = new Player();
        player.load();
        projectiles = new ArrayList<>(); // Lista pros projeteis
        collectibles = new ArrayList<>(); // Lista dos coletáveis
        enemies = new ArrayList<>(); // Lista de inimigos

        //Pra outros sprites, adicionar aqui
        scoreStrategy = new CommonScoreStrategy();

        addKeyListener(new TecladoAdapter());

        timer = new Timer(5, this);
        timer.start();
        gameTimer.start();
        countdownTimer.start();
        spawnTimer.start();
        enemySpawnTimer.start();
    }

    private void spawnDiamond() {
        if (!isStarted || isGameOver) {
            return;
        }

        // Posição aleatória na tela
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

        // Define o tipo de diamante baseado em probabilidade
        int chance = rand.nextInt(100);
        Collectible.DiamondType type;

        if (chance < 60) {
            type = Collectible.DiamondType.COMUM; // 60% chance
        } else if (chance < 90) {
            type = Collectible.DiamondType.RARO; // 30% chance
        } else {
            type = Collectible.DiamondType.LENDARIO; // 10% chance
        }

        collectibles.add(new Collectible(x, y, type));
    }

    /**
     * Spawna um inimigo em posição aleatória
     */
    private void spawnEnemy() {
        if (!isStarted || isGameOver) {
            return;
        }

        // Posição aleatória na tela
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

        // Cria um inimigo aleatório usando a Factory
        enemies.add(EnemyFactory.createRandomEnemy(x, y));
    }

    private void checkCollisions() {
        Rectangle playerBounds = new Rectangle(
                player.getX(),
                player.getY(),
                player.getLargura(),
                player.getAltura()
        );

        Iterator<Collectible> it = collectibles.iterator();
        while (it.hasNext()) {
            Collectible c = it.next();
            if (c.isVisivel() && playerBounds.intersects(c.getBounds())) {
                // Usa a estratégia para calcular pontuação
                score += scoreStrategy.calculateScore(c);
                c.setVisivel(false);
                it.remove();
            }
        }
    }

    /**
     * Verifica colisões entre projéteis e inimigos
     */
    private void checkProjectileEnemyCollisions() {
        Iterator<Projectile> projIt = projectiles.iterator();

        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            Rectangle projBounds = new Rectangle(
                    proj.getX(),
                    proj.getY(),
                    proj.getLargura(),
                    proj.getAltura()
            );

            Iterator<Enemy> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();

                if (enemy.isVisivel() && projBounds.intersects(enemy.getBounds())) {
                    // Inimigo leva dano
                    enemy.takeDamage(50); // 50 de dano por projétil

                    // Remove o projétil
                    projIt.remove();

                    // Se o inimigo morreu, adiciona score
                    if (!enemy.isVisivel()) {
                        score += enemy.getScore();
                        enemyIt.remove();
                    }

                    break; // Sai do loop de inimigos para este projétil
                }
            }
        }
    }

    /**
     * Verifica colisões entre player e inimigos
     */
    private void checkPlayerEnemyCollisions() {
        Rectangle playerBounds = new Rectangle(
                player.getX(),
                player.getY(),
                player.getLargura(),
                player.getAltura()
        );

        for (Enemy enemy : enemies) {
            if (enemy.isVisivel() && playerBounds.intersects(enemy.getBounds())) {
                // TODO: Implementar sistema de vida do player
                // Por enquanto, apenas game over
                isGameOver = true;
                setStarted(false);
                countdownTimer.stop();
                gameTimer.stop();
                enemySpawnTimer.stop();
                break;
            }
        }
    }

    /**
     * Verifica se o player está colidindo com algum tile sólido
     * @return true se houver colisão, false caso contrário
     */
    private boolean checkPlayerTileCollision() {
        // Define a hitbox do jogador (ajuste as margens conforme necessário)
        // Para um sprite de 16x16, uma margem de 2-4 pixels é boa
        int hitboxMargin = 3;
        int hitboxX = player.getX() + hitboxMargin;
        int hitboxY = player.getY() + hitboxMargin;
        int hitboxWidth = player.getLargura() - (hitboxMargin * 2);
        int hitboxHeight = player.getAltura() - (hitboxMargin * 2);

        // Calcula em quais tiles os 4 cantos da hitbox estão
        int leftCol = hitboxX / tileSize;
        int rightCol = (hitboxX + hitboxWidth) / tileSize;
        int topRow = hitboxY / tileSize;
        int bottomRow = (hitboxY + hitboxHeight) / tileSize;

        // Garante que não saia dos limites do mapa
        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true; // Considera como colisão se sair do mapa
        }

        // Verifica os 4 tiles ao redor do jogador
        int tileNum1 = tileM.mapTileNum[leftCol][topRow];      // Canto superior esquerdo
        int tileNum2 = tileM.mapTileNum[rightCol][topRow];     // Canto superior direito
        int tileNum3 = tileM.mapTileNum[leftCol][bottomRow];   // Canto inferior esquerdo
        int tileNum4 = tileM.mapTileNum[rightCol][bottomRow];  // Canto inferior direito

        // Verifica se algum dos tiles tem colisão
        return tileM.tile[tileNum1].collision ||
                tileM.tile[tileNum2].collision ||
                tileM.tile[tileNum3].collision ||
                tileM.tile[tileNum4].collision;
    }

    /**
     * Verifica colisão de um projétil com tiles
     * @param p O projétil a verificar
     * @return true se houver colisão
     */
    private boolean checkProjectileTileCollision(Projectile p) {
        // Calcula tiles ocupados pelo projétil
        int leftCol = p.getX() / tileSize;
        int rightCol = (p.getX() + p.getLargura()) / tileSize;
        int topRow = p.getY() / tileSize;
        int bottomRow = (p.getY() + p.getAltura()) / tileSize;

        // Garante que está dentro dos limites
        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        // Verifica os tiles ao redor do projétil
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

    /**
     * Verifica colisão em uma área específica (útil para spawn)
     * @param x Posição X
     * @param y Posição Y
     * @param width Largura
     * @param height Altura
     * @return true se houver colisão
     */
    private boolean checkTileCollisionAt(int x, int y, int width, int height) {
        int leftCol = x / tileSize;
        int rightCol = (x + width) / tileSize;
        int topRow = y / tileSize;
        int bottomRow = (y + height) / tileSize;

        // Garante que está dentro dos limites
        if (leftCol < 0 || rightCol >= maxScreenCol || topRow < 0 || bottomRow >= maxScreenRow) {
            return true;
        }

        // Verifica todos os tiles na área
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

    private void updateTime() {
        if (this.isStarted == true) {
            if (timeLeft > 0) {
                timeLeft--;
            } else {
                isGameOver = true;
                setStarted(false);
                countdownTimer.stop();
                gameTimer.stop();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graficos = (Graphics2D) g;

        // Desenha os tiles
        tileM.draw(graficos);

        // Anti-aliasing para melhor qualidade
        graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o player
        if (player.getImagem() != null) {
            graficos.drawImage(player.getImagem(), player.getX(), player.getY(), this);

            // ========== DEBUG: Desenha hitbox (remova depois de testar) ==========
            graficos.setColor(new Color(255, 0, 0, 100)); // Vermelho transparente
            int hitboxMargin = 3;
            graficos.drawRect(
                    player.getX() + hitboxMargin,
                    player.getY() + hitboxMargin,
                    player.getLargura() - (hitboxMargin * 2),
                    player.getAltura() - (hitboxMargin * 2)
            );
        }

        // Desenha os projeteis
        for (Projectile projectile : projectiles) {
            if (projectile.getImagem() != null) {
                graficos.drawImage(projectile.getImagem(), projectile.getX(), projectile.getY(), this);
            }
        }

        // Desenha os coletáveis
        for (Collectible collectible : collectibles) {
            if (collectible.isVisivel() && collectible.getImagem() != null) {
                graficos.drawImage(collectible.getImagem(), collectible.getX(), collectible.getY(), this);
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isVisivel() && enemy.getImagem() != null) {
                graficos.drawImage(enemy.getImagem(), enemy.getX(), enemy.getY(), this);

                // ========== DEBUG: Desenha barra de vida ==========
                graficos.setColor(Color.RED);
                graficos.fillRect(enemy.getX(), enemy.getY() - 5, enemy.getLargura(), 3);
                graficos.setColor(Color.GREEN);
                int healthWidth = (int) (enemy.getLargura() * (enemy.getHealth() / 100.0));
                graficos.fillRect(enemy.getX(), enemy.getY() - 5, healthWidth, 3);
            }
        }

        if (isGameOver) { // TODO fix tela de game over
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Pontuação: " + score, getWidth() / 2 - 70, getHeight() / 2 + 40);

            return;

        }

        // HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Pontuação: " + score, 20, 30);
        g.drawString("Tempo: " + formatTime(timeLeft), 20, 55);

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

            if(checkPlayerTileCollision()){
                player.setX(oldX);
                player.setY(oldY);
            }
            // Update nos projéteis e remove fora da tela
            Iterator<Projectile> it = projectiles.iterator();
            while (it.hasNext()) {
                Projectile p = it.next();
                p.update();
                //Se colidir com uma parede ele desaparece
                if (checkProjectileTileCollision(p)){
                    it.remove();
                    continue;
                }
                if (!p.isVisible()) {
                    it.remove();
                }
            }

            // NOVO: Update nos inimigos
            for (Enemy enemy : enemies) {
                if (enemy.isVisivel()) {
                    int oldEnemyX = enemy.getX();
                    int oldEnemyY = enemy.getY();

                    enemy.update(player.getX(), player.getY());

                    // Verifica colisão do inimigo com tiles
                    if (checkTileCollisionAt(enemy.getX(), enemy.getY(),
                            enemy.getLargura(), enemy.getAltura())) {
                        enemy.setX(oldEnemyX);
                        enemy.setY(oldEnemyY);
                    }
                }
            }
            // Verifica colisões
            checkCollisions();
            checkProjectileEnemyCollisions();
            checkPlayerEnemyCollisions();
        }

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
                if (apertinho == false) {
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

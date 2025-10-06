package Modelo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameplayPanel extends JPanel implements ActionListener {
    
    private Timer gameTimer;
    private Timer countdownTimer;
    private int timeLeft = 120;
    private boolean isGameOver = false;
    private boolean isStarted = false;

    private int score = 0;


    private ArrayList<Projectile> projectiles;
    private Player player;
    private Timer timer;

    public GameplayPanel() {
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        //Timer do jogo
        gameTimer = new Timer(16, this); 
        countdownTimer = new Timer(1000, e -> updateTime());

        player = new Player();
        player.load();
        projectiles = new ArrayList<>(); // Lista pros projeteis
        //Pra outros sprites, adicionar aqui


        addKeyListener(new TecladoAdapter());

        timer = new Timer(5, this);
        timer.start();
        gameTimer.start();
        countdownTimer.start();
        
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
        
        // Anti-aliasing para melhor qualidade
        graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                  RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha o player
        if(player.getImagem() != null) {
            graficos.drawImage(player.getImagem(), player.getX(), player.getY(), this);
        }
        
        // Desenha os projeteis
        for(Projectile projectile : projectiles) {
            if(projectile.getImagem() != null) {
                graficos.drawImage(projectile.getImagem(), projectile.getX(), projectile.getY(), this);
            }
        }

        if (isGameOver) { // Tela de Game Over 
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
        int min = seconds / 60; int sec = seconds % 60; 
        return String.format("%02d:%02d", min, sec);
     }


    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        // Update nos projetil e remove fora da tela
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();
            if (!p.isVisible()) {
                it.remove();
            }
        }

        //Adicionar aqui os sprites dos inimigos tambem

        repaint();
    }

    private class TecladoAdapter extends KeyAdapter {
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

                projectiles.add(new Projectile(
                    player.getX() + (player.getLargura() / 2),
                    player.getY() + (player.getAltura() / 2),
                    projDx,
                    projDy 
                ));
            } else {
                player.keyPressed(e);
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e);
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    
}
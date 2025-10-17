package Modelo.UI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import AramariRUSH.Container;

public class GameOverScreen {
    
    private Image spriteGameOver;
    private JButton btnJogarNovamente;
    private JButton btnVoltarMenu;
    private boolean isVisible;
    private int score;
    private Container containerRef;
    private JPanel parentPanel;
    
    public GameOverScreen(Container container, JPanel parent) {
        this.containerRef = container;
        this.parentPanel = parent;
        this.isVisible = false;
        this.score = 0;
        
        // Carrega o sprite do Game Over
        ImageIcon gameOverIcon = new ImageIcon("src\\res\\gameover_sprite.png");
        spriteGameOver = gameOverIcon.getImage();
        
        criarBotoes();
    }
    
    private void criarBotoes() {
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
        
        parentPanel.add(btnJogarNovamente);
        parentPanel.add(btnVoltarMenu);
    }
    
    public void show(int finalScore, Runnable onRestart, Runnable onBackToMenu) {
        this.score = finalScore;
        this.isVisible = true;
        
        posicionarBotoes();
        btnJogarNovamente.setVisible(true);
        btnVoltarMenu.setVisible(true);
        
        // Remove listeners anteriores
        for (var listener : btnJogarNovamente.getActionListeners()) {
            btnJogarNovamente.removeActionListener(listener);
        }
        for (var listener : btnVoltarMenu.getActionListeners()) {
            btnVoltarMenu.removeActionListener(listener);
        }
        
        // Adiciona novos listeners
        btnJogarNovamente.addActionListener(e -> {
            hide();
            onRestart.run();
        });
        
        btnVoltarMenu.addActionListener(e -> {
            hide();
            onBackToMenu.run();
        });
    }
    
    public void hide() {
        this.isVisible = false;
        btnJogarNovamente.setVisible(false);
        btnVoltarMenu.setVisible(false);
    }
    
    private void posicionarBotoes() {
        int btnWidth = 250;
        int btnHeight = 60;
        int centerX = parentPanel.getWidth() / 2 - btnWidth / 2;
        int centerY = parentPanel.getHeight() / 2 + 80;
        int spacing = 20;
        
        btnJogarNovamente.setBounds(centerX, centerY, btnWidth, btnHeight);
        btnVoltarMenu.setBounds(centerX, centerY + btnHeight + spacing, btnWidth, btnHeight);
    }
    
    public void draw(Graphics2D g2, int panelWidth, int panelHeight) {
        if (!isVisible) return;
        
        // Desenha o sprite do Game Over (400x300)
        if (spriteGameOver != null) {
            int spriteWidth = 400;
            int spriteHeight = 300;
            int x = (panelWidth - spriteWidth) / 2;
            int y = (panelHeight / 2) - spriteHeight / 2 - 100;
            
            g2.drawImage(spriteGameOver, x, y, spriteWidth, spriteHeight, null);
        }
        
        // Desenha a pontuação
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String scoreText = "Pontuação: " + score;
        int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
        g2.drawString(scoreText, panelWidth / 2 - scoreWidth / 2, panelHeight / 2 + 10);
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
}
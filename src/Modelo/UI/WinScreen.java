package Modelo.UI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import AramariRUSH.Container;

public class WinScreen {
    
    private Image spriteYouWin;
    private Image spriteJogarNovamente;
    private Image spriteSair;
    private boolean isVisible;
    private int score;
    private Container containerRef;
    private JPanel parentPanel;
    
    // Áreas clicáveis dos botões
    private Rectangle btnJogarNovamenteRect;
    private Rectangle btnSairRect;
    
    // Callbacks
    private Runnable onRestart;
    private Runnable onBackToMenu;
    
    // Estados de hover
    private boolean hoverJogarNovamente;
    private boolean hoverSair;
    
    public WinScreen(Container container, JPanel parent) {
        this.containerRef = container;
        this.parentPanel = parent;
        this.isVisible = false;
        this.score = 0;
        this.hoverJogarNovamente = false;
        this.hoverSair = false;
        
        // Carrega os sprites
        ImageIcon youWinIcon = new ImageIcon("src\\res\\youwin_sprite.png");
        spriteYouWin = youWinIcon.getImage();
        
        ImageIcon jogarNovamenteIcon = new ImageIcon("src\\res\\jogardenovo_sprite.png");
        spriteJogarNovamente = jogarNovamenteIcon.getImage();
        
        ImageIcon sairIcon = new ImageIcon("src\\res\\sair_sprite.png");
        spriteSair = sairIcon.getImage();
        
        configurarMouseListener();
    }
    
    private void configurarMouseListener() {
        parentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isVisible) return;
                
                Point clickPoint = e.getPoint();
                
                if (btnJogarNovamenteRect != null && btnJogarNovamenteRect.contains(clickPoint)) {
                    hide();
                    if (onRestart != null) {
                        onRestart.run();
                    }
                } else if (btnSairRect != null && btnSairRect.contains(clickPoint)) {
                    hide();
                    if (onBackToMenu != null) {
                        onBackToMenu.run();
                    }
                }
            }
        });
        
        parentPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isVisible) return;
                
                Point mousePoint = e.getPoint();
                
                boolean wasHoverJogar = hoverJogarNovamente;
                boolean wasHoverSair = hoverSair;
                
                hoverJogarNovamente = btnJogarNovamenteRect != null && btnJogarNovamenteRect.contains(mousePoint);
                hoverSair = btnSairRect != null && btnSairRect.contains(mousePoint);
                
                // Muda o cursor
                if (hoverJogarNovamente || hoverSair) {
                    parentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    parentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                
                // Repinta se o estado de hover mudou
                if (wasHoverJogar != hoverJogarNovamente || wasHoverSair != hoverSair) {
                    parentPanel.repaint();
                }
            }
        });
    }
    
    public void show(int finalScore, Runnable onRestart, Runnable onBackToMenu) {
        this.score = finalScore;
        this.isVisible = true;
        this.onRestart = onRestart;
        this.onBackToMenu = onBackToMenu;
        
        calcularPosicoesBotoes();
    }
    
    public void hide() {
        this.isVisible = false;
        this.hoverJogarNovamente = false;
        this.hoverSair = false;
        parentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void calcularPosicoesBotoes() {
        int btnWidth = 250;
        int btnHeight = 80;
        int centerX = parentPanel.getWidth() / 2 - btnWidth / 2;
        int centerY = parentPanel.getHeight() / 2 + 80;
        int spacing = 20;
        
        btnJogarNovamenteRect = new Rectangle(centerX, centerY, btnWidth, btnHeight);
        btnSairRect = new Rectangle(centerX, centerY + btnHeight + spacing, btnWidth, btnHeight);
    }
    
    public void draw(Graphics2D g2, int panelWidth, int panelHeight) {
        if (!isVisible) return;
        
        // Recalcula posições se necessário
        if (btnJogarNovamenteRect == null) {
            calcularPosicoesBotoes();
        }
        
        // Desenha o sprite You Win (400x300)
        if (spriteYouWin != null) {
            int spriteWidth = 400;
            int spriteHeight = 300;
            int x = (panelWidth - spriteWidth) / 2;
            int y = (panelHeight / 2) - spriteHeight / 2 - 100;
            
            g2.drawImage(spriteYouWin, x, y, spriteWidth, spriteHeight, null);
        }
        
        // Desenha a pontuação
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String scoreText = "Pontuação: " + score;
        int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
        g2.drawString(scoreText, panelWidth / 2 - scoreWidth / 2, panelHeight / 2 + 10);
        
        // Desenha o botão Jogar Novamente
        if (spriteJogarNovamente != null && btnJogarNovamenteRect != null) {
            // Efeito de hover (aumenta levemente o brilho)
            if (hoverJogarNovamente) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            }
            g2.drawImage(spriteJogarNovamente, 
                        btnJogarNovamenteRect.x, 
                        btnJogarNovamenteRect.y, 
                        btnJogarNovamenteRect.width, 
                        btnJogarNovamenteRect.height, 
                        null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Desenha o botão Sair
        if (spriteSair != null && btnSairRect != null) {
            // Efeito de hover (aumenta levemente o brilho)
            if (hoverSair) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            }
            g2.drawImage(spriteSair, 
                        btnSairRect.x, 
                        btnSairRect.y, 
                        btnSairRect.width, 
                        btnSairRect.height, 
                        null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
}
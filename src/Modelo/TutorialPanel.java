package Modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import AramariRUSH.Container;
import Modelo.Audio.SoundManager;
import Modelo.UI.ButtonFactory;

public class TutorialPanel extends JPanel {

    private Image tutorialImage;
    private Image spriteJogar;
    private Image spriteSair;

    private JButton btnJogar;
    private JButton btnSair;
    private Container containerRef;
    private boolean imagemCarregada = false;

    private SoundManager soundManager;

    public TutorialPanel(Container container) {
        this.containerRef = container;
        this.soundManager = SoundManager.getInstance();

        // Carrega os sprites dos botões
        ImageIcon jogarIcon = new ImageIcon(getClass().getResource("/res/jogar_sprite.png"));
        spriteJogar = jogarIcon.getImage();

        ImageIcon sairIcon = new ImageIcon(getClass().getResource("/res/sair_sprite.png"));
        spriteSair = sairIcon.getImage();

        setFocusable(true);
        setDoubleBuffered(true);
        setLayout(null);
        setBackground(Color.decode("#D4671B"));

        carregarImagem();
        criarBotoes();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    voltarMenu();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    iniciarJogo();
                }
            }
        });
    }

    private void carregarImagem() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/res/Tutorial.png"));
            tutorialImage = icon.getImage();
            imagemCarregada = true;
            System.out.println("✅ Tutorial carregado com sucesso!");
        } catch (Exception e) {
            System.err.println("⚠️ Imagem do tutorial não encontrada!");
            imagemCarregada = false;
        }
    }

    private void criarBotoes() {
        // Cria o botão Jogar usando a ButtonFactory
        btnJogar = ButtonFactory.createButton(spriteJogar);
        btnJogar.addActionListener(e -> {
            soundManager.playSound("button");
            iniciarJogo();
        });
        add(btnJogar);

        // Cria o botão Sair usando a ButtonFactory
        btnSair = ButtonFactory.createButton(spriteSair);
        btnSair.addActionListener(e -> {
            soundManager.playSound("button");
            voltarMenu();
        });
        add(btnSair);
    }

    private void posicionarBotoes() {
        int btnWidth = 200;
        int btnHeight = 60;
        int spacing = 20;
        int totalWidth = (btnWidth * 2) + spacing;
        int startX = (getWidth() - totalWidth) / 2;
        int y = getHeight() - 100;

        btnSair.setBounds(startX, y, btnWidth, btnHeight);
        btnJogar.setBounds(startX + btnWidth + spacing, y, btnWidth, btnHeight);
    }

    private void voltarMenu() {
        containerRef.showScreen("Menu");
    }

    private void iniciarJogo() {
        containerRef.showScreen("Gameplay");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (imagemCarregada && tutorialImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = tutorialImage.getWidth(this);
            int imgH = tutorialImage.getHeight(this);

            if (imgW > 0 && imgH > 0) {
                double imgAspect = (double) imgW / imgH;
                double panelAspect = (double) panelW / panelH;

                int drawW, drawH;

                if (panelAspect > imgAspect) {
                    drawH = panelH - 150;
                    drawW = (int) (drawH * imgAspect);
                } else {
                    drawW = panelW - 100;
                    drawH = (int) (drawW / imgAspect);
                }

                int x = (panelW - drawW) / 2;
                int y = (panelH - drawH - 100) / 2;

                g2.drawImage(tutorialImage, x, y, drawW, drawH, this);

            }
        }
        posicionarBotoes();
    }
}

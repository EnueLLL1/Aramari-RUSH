package Modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import AramariRUSH.Container;
import Modelo.Audio.SoundManager;
import Modelo.UI.ButtonFactory;

public class MenuPanel extends JPanel {

    private final transient Image fundoTela;
    private final transient Image spriteTitulo;
    private final transient Image spriteJogar;
    private final transient Image spriteSair;
    private final transient Image spriteTutorial;
    private final JButton btnJogar;
    private final JButton btnSair;
    private final JButton btnTutorial;
    private final transient SoundManager soundManager;

    public MenuPanel(Container window) {
        // Carrega a imagem de fundo
        ImageIcon fundoIcon = new ImageIcon(getClass().getResource("/res/fundo_menu.png"));
        this.fundoTela = fundoIcon.getImage();

        // Carrega o título
        ImageIcon tituloIcon = new ImageIcon(getClass().getResource("/res/titulo_sprite.png"));
        this.spriteTitulo = tituloIcon.getImage();

        // Carrega os sprites dos botões
        ImageIcon jogarIcon = new ImageIcon(getClass().getResource("/res/jogar_sprite.png"));
        this.spriteJogar = jogarIcon.getImage();

        // Carrega o sprite do butão de Tutorial
        ImageIcon tutorialIcon = new ImageIcon(getClass().getResource("/res/tutorial_sprite.png"));
        this.spriteTutorial = tutorialIcon.getImage();

        ImageIcon sairIcon = new ImageIcon(getClass().getResource("/res/sair_sprite.png"));
        this.spriteSair = sairIcon.getImage();

        // Configura o layout
        setLayout(null);
        setFocusable(true);
        setDoubleBuffered(true);

        // Inicializa o gerenciador de som
        this.soundManager = SoundManager.getInstance();

        // Cria os botões usando a ButtonFactory
        this.btnJogar = ButtonFactory.createButton(spriteJogar);
        this.btnTutorial = ButtonFactory.createButton(spriteTutorial);
        this.btnSair = ButtonFactory.createButton(spriteSair);

        // Configura as ações dos botões
        btnJogar.addActionListener(e -> {
            soundManager.playSound("button");
            window.showScreen("Gameplay");
        });

        btnTutorial.addActionListener(e -> {
            soundManager.playSound("button");
            window.showScreen("Tutorial");
        });

        btnSair.addActionListener(e -> {
            soundManager.playSound("button");
            int resposta = JOptionPane.showConfirmDialog(MenuPanel.this,
                    "Você tem certeza que vai deixar Aramari?",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                soundManager.cleanup();
                System.exit(0);
            }
        });

        // Adiciona os botões ao painel
        add(btnJogar);
        add(btnTutorial);
        add(btnSair);
    }

    private void posicionarBotoes() {
        int btnWidth = 200;
        int btnHeight = 60;
        int spacing = 20;
        
        // Calcula a posição Y considerando o título
        int tituloHeight = (int) (Math.min(400, getWidth() - 40) * 0.75); // Altura do título
        int tituloY = getHeight() / 8; // Posição Y do título
        int tituloBottom = tituloY + tituloHeight;
        
        // Posiciona os botões abaixo do título com margem de 60px
        int startY = tituloBottom + 60;
        int x = (getWidth() - btnWidth) / 2;

        // Posiciona os botões abaixo do título
        btnJogar.setBounds(x, startY, btnWidth, btnHeight);
        btnTutorial.setBounds(x, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnSair.setBounds(x, startY + (btnHeight + spacing) * 2, btnWidth, btnHeight);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int panelW = getWidth();
        int panelH = getHeight();

        // Fundo responsivo
        if (fundoTela != null) {
            int imgW = fundoTela.getWidth(this);
            int imgH = fundoTela.getHeight(this);

            if (imgW > 0 && imgH > 0) {
                double imgAspect = (double) imgW / imgH;
                double panelAspect = (double) panelW / panelH;

                int drawW;
                int drawH;

                if (panelAspect > imgAspect) {
                    drawH = panelH;
                    drawW = (int) (drawH * imgAspect);
                } else {
                    drawW = panelW;
                    drawH = (int) (drawW / imgAspect);
                }

                int x = (panelW - drawW) / 2;
                int y = (panelH - drawH) / 2;

                g2.drawImage(fundoTela, x, y, drawW, drawH, this);
            }
        } else {
            g2.setColor(new Color(25, 25, 112));
            g2.fillRect(0, 0, panelW, panelH);
        }

        // Título com tamanho responsivo
        if (spriteTitulo != null) {
            int tituloW = Math.min(400, panelW - 40); // Máximo 400px, mas não ultrapassa a largura
            int tituloH = (int) (tituloW * 0.75); // Mantém proporção 4:3

            int x = (panelW - tituloW) / 2; // centraliza horizontalmente
            int y = panelH / 8;             // margem superior menor

            g2.drawImage(spriteTitulo, x, y, tituloW, tituloH, this);
        }

        // Posiciona os botões de forma responsiva
        posicionarBotoes();

        g2.dispose();
    }
}
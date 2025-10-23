package Modelo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import AramariRUSH.Container;
import Modelo.Audio.SoundManager;

public class MenuPanel extends JPanel {

    private final transient Image fundoTela;
    private final transient Image spriteTitulo;
    private final transient Image spriteJogar;
    private final transient Image spriteSair;
    private final JButton btnJogar;
    private final JButton btnSair;
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

        ImageIcon sairIcon = new ImageIcon(getClass().getResource("/res/sair_sprite.png"));
        this.spriteSair = sairIcon.getImage();

        // Configura o layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Inicializa o gerenciador de som
        this.soundManager = SoundManager.getInstance();

        // Cria os botões
        this.btnJogar = new JButton();
        this.btnSair = new JButton();

        // Configura os botões
        configurarBotao(btnJogar, spriteJogar);
        configurarBotao(btnSair, spriteSair);

        // Configura as ações dos botões
        btnJogar.addActionListener(e -> {
            soundManager.playSound("button");
            window.showScreen("Gameplay");
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

        // Posicionamento dos botões
        gbc.insets = new Insets(380, 20, 15, 20); // Margem superior maior para não sobrepor o título
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 0;
        add(btnJogar, gbc);

        gbc.insets = new Insets(15, 20, 15, 20); // Espaçamento normal entre os botões
        gbc.gridy = 1;
        add(btnSair, gbc);
    }

    private void configurarBotao(JButton botao, Image sprite) {
        // Remove bordas e fundo padrão
        botao.setBorder(null);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);

        // Ícone com tamanho fixo de 200x60
        if (sprite != null) {
            ImageIcon scaledIcon = new ImageIcon(sprite.getScaledInstance(200, 60, Image.SCALE_SMOOTH));
            botao.setIcon(scaledIcon);
            botao.setPreferredSize(new Dimension(200, 60));
        }

        // Adiciona efeito hover (inchação)
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (sprite != null) {
                    // Aumenta 10% no hover (220x66)
                    ImageIcon hoverIcon = new ImageIcon(sprite.getScaledInstance(220, 66, Image.SCALE_SMOOTH));
                    botao.setIcon(hoverIcon);
                }
                botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (sprite != null) {
                    // Volta ao tamanho normal (200x60)
                    ImageIcon normalIcon = new ImageIcon(sprite.getScaledInstance(200, 60, Image.SCALE_SMOOTH));
                    botao.setIcon(normalIcon);
                }
                botao.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
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

        // Título com tamanho fixo de 400x300
        if (spriteTitulo != null) {
            int tituloW = 400;
            int tituloH = 300;

            int x = (panelW - tituloW) / 2; // centraliza horizontalmente
            int y = panelH / 10;            // margem superior

            g2.drawImage(spriteTitulo, x, y, tituloW, tituloH, this);
        }

        g2.dispose();
    }
}
package Modelo;

import AramariRUSH.Container;
import Modelo.Audio.SoundManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private Image fundoTela, spriteTitulo;
    private JButton btnJogar, btnSair;
    private Image spriteJogar, spriteSair;
    private SoundManager soundManager;

    public MenuPanel(Container window) {

        soundManager = SoundManager.getInstance();

        // Carrega a imagem de fundo
        ImageIcon pegaimagem = new ImageIcon("src//res//fundo_menu.png");
        fundoTela = pegaimagem.getImage();

        // Carrega o sprite do título
        ImageIcon tituloIcon = new ImageIcon("src//res//titulo_sprite.png");
        spriteTitulo = tituloIcon.getImage();

        // Carrega os sprites dos botões
        ImageIcon jogarIcon = new ImageIcon("src//res//jogar_sprite.png");
        spriteJogar = jogarIcon.getImage();
        ImageIcon sairIcon = new ImageIcon("src//res//sair_sprite.png");
        spriteSair = sairIcon.getImage();

        // Configura o layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Cria os botões
        btnJogar = new JButton();
        btnSair = new JButton();

        // Estiliza os botões com tamanho fixo de 200x60
        estilizarBotao(btnJogar, spriteJogar);
        estilizarBotao(btnSair, spriteSair);

        // Ações dos botões
        btnJogar.addActionListener(e -> {
            soundManager.playSound("button"); // Som de botão
            window.showScreen("Gameplay");
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundManager.playSound("button"); // Som de botão
                int resposta = JOptionPane.showConfirmDialog(MenuPanel.this,
                        "Você tem certeza que vai deixar Aramari?",
                        "",
                        JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    soundManager.cleanup(); // Limpa recursos de áudio
                    System.exit(0);
                }
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

    public void estilizarBotao(JButton botao, Image sprite) {
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

                int drawW, drawH;

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
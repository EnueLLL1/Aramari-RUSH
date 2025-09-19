package Modelo;

import AramariRUSH.Container;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private Image fundoTela, spriteTitulo;
    private JButton btnJogar, btnSair;
    private Image spriteJogar, spriteSair;

    public MenuPanel(Container window) {

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

        // Estiliza os botões inicialmente
        estilizarBotao(btnJogar, spriteJogar);
        estilizarBotao(btnSair, spriteSair);

        // Ações dos botões
        btnJogar.addActionListener(e -> {
            window.showScreen("Gameplay");
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(MenuPanel.this,
                        "Você tem certeza que vai deixar Aramari?",
                        "",
                        JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Posicionamento dos botões
        gbc.insets = new Insets(30, 20, 30, 20);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 0;
        add(btnJogar, gbc);

        gbc.gridy = 1;
        add(btnSair, gbc);
    }

    private void estilizarBotao(JButton botao, Image sprite) {
        // Remove bordas e fundo padrão
        botao.setBorder(null);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);

        // Ícone inicial
        if (sprite != null) {
            ImageIcon scaledIcon = new ImageIcon(sprite.getScaledInstance(200, 60, Image.SCALE_SMOOTH));
            botao.setIcon(scaledIcon);
        }
    }

    // Redimensiona os botões conforme o tamanho do painel
    @Override
    public void doLayout() {
        super.doLayout();
        int panelW = getWidth();
        int panelH = getHeight();

        int larguraBtn = panelW / 4;  // 1/4 da largura do painel
        int alturaBtn = panelH / 10;  // 1/10 da altura do painel

        redimensionarBotao(btnJogar, spriteJogar, larguraBtn, alturaBtn);
        redimensionarBotao(btnSair, spriteSair, larguraBtn, alturaBtn);
    }

    private void redimensionarBotao(JButton botao, Image sprite, int largura, int altura) {
        if (sprite != null && largura > 0 && altura > 0) {
            ImageIcon scaledIcon = new ImageIcon(sprite.getScaledInstance(largura, altura, Image.SCALE_SMOOTH));
            botao.setIcon(scaledIcon);
            botao.setPreferredSize(new Dimension(largura, altura));
        }
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

        // Título responsivo
        if (spriteTitulo != null) {
            int targetW = panelW / 3;   // 1/3 da largura da tela
            int targetH = panelH / 6;   // proporcional à altura

            int x = (panelW - targetW) / 2; // centraliza
            int y = panelH / 10;            // margem superior

            g2.drawImage(spriteTitulo, x, y, targetW, targetH, this);
        }

        g2.dispose();
    }
}

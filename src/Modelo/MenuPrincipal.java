package Modelo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuPrincipal extends JPanel {

    private Image fundoTela;
    private JButton btnJogar, btnSair;
    private Image spriteJogar, spriteSair;

    public MenuPrincipal() {
        // Carrega a imagem de fundo
        ImageIcon pegaimagem = new ImageIcon("src//res//fundo_menu.png");
        fundoTela = pegaimagem.getImage();
        
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
        
        // Estiliza os botões
        estilizarBotao(btnJogar, spriteJogar);
        estilizarBotao(btnSair, spriteSair);
        
        // Adiciona ações aos botões
        btnJogar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuPrincipal.this, "Iniciando o jogo...");
                // TODO: Mudar para a tela do jogo rodando
            }
        });
        
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(MenuPrincipal.this, 
                    "Tem certeza que deseja sair?", 
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
        if (sprite != null) {
            // Define o tamanho do botão baseado no sprite
            int largura = sprite.getWidth(null);
            int altura = sprite.getHeight(null);
            
            // Escala o botão se necessário
            if (largura > 250) {
                double escala = 250.0 / largura;
                largura = (int)(largura * escala);
                altura = (int)(altura * escala);
            }
            
            botao.setPreferredSize(new Dimension(largura, altura));
            
            // Remove bordas e backgrounds padrão
            botao.setBorder(null);
            botao.setContentAreaFilled(false);
            botao.setFocusPainted(false);
            botao.setOpaque(false);
            
            // Define o ícone do botão
            final int larguraFinal = largura;
            final int alturaFinal = altura; 
                ImageIcon scaledIcon = new ImageIcon(sprite.getScaledInstance(larguraFinal, alturaFinal, Image.SCALE_SMOOTH));
                botao.setIcon(scaledIcon);
            
            // Efeito hover
            botao.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        ImageIcon hoveredIcon = new ImageIcon(sprite.getScaledInstance(
                            (int)(larguraFinal * 1.05), (int)(alturaFinal * 1.05), Image.SCALE_SMOOTH));
                        botao.setIcon(hoveredIcon);
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        botao.setIcon(scaledIcon);
                    }
                });
        } else {
            //caso a imagem não carregue
            botao.setPreferredSize(new Dimension(200, 50));
            botao.setFont(new Font("Arial", Font.BOLD, 18));
            botao.setForeground(Color.WHITE);
            botao.setBackground(new Color(0, 0, 0, 150));
            botao.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            botao.setFocusPainted(false);
            botao.setOpaque(false);
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

        // Desenha o fundo
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
            // Fundo alternativo
            g2.setColor(new Color(25, 25, 112));
            g2.fillRect(0, 0, panelW, panelH);
        }

        //TODO: FAZER A IMAGEM DE TITULO FUNCIONAR (TORTURA)
        // Título do jogo
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String titulo = "DIAMONDS FOR THE QUEEN";
        FontMetrics fm = g2.getFontMetrics();
        int tituloX = (panelW - fm.stringWidth(titulo)) / 2;
        int tituloY = 100;

        // Sombra do título
        g2.setColor(Color.BLACK);
        g2.drawString(titulo, tituloX + 2, tituloY + 2);
        
        // Título
        g2.setColor(Color.YELLOW);
        g2.drawString(titulo, tituloX, tituloY);

        g2.dispose();
    }
}
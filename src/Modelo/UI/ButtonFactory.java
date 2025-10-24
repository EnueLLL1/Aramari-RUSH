package Modelo.UI;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Factory class para criar botões reutilizáveis com configurações consistentes
 * em todo o jogo.
 */
public class ButtonFactory {
    
    /**
     * Cria um botão com sprite, efeitos hover e configurações padrão
     * @param sprite A imagem do sprite do botão
     * @param width Largura do botão
     * @param height Altura do botão
     * @return JButton configurado
     */
    public static JButton createButton(Image sprite, int width, int height) {
        JButton button = new JButton();
        
        // Remove bordas e fundo padrão
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
        // Configura o ícone com tamanho especificado
        if (sprite != null) {
            ImageIcon scaledIcon = new ImageIcon(sprite.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            button.setIcon(scaledIcon);
            button.setPreferredSize(new Dimension(width, height));
        }
        
        // Adiciona efeito hover (aumenta 10% no hover)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (sprite != null) {
                    int hoverWidth = (int) (width * 1.1);
                    int hoverHeight = (int) (height * 1.1);
                    ImageIcon hoverIcon = new ImageIcon(sprite.getScaledInstance(hoverWidth, hoverHeight, Image.SCALE_SMOOTH));
                    button.setIcon(hoverIcon);
                }
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (sprite != null) {
                    ImageIcon normalIcon = new ImageIcon(sprite.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                    button.setIcon(normalIcon);
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    /**
     * Cria um botão com tamanho padrão (200x60)
     * @param sprite A imagem do sprite do botão
     * @return JButton configurado
     */
    public static JButton createButton(Image sprite) {
        return createButton(sprite, 200, 60);
    }
}



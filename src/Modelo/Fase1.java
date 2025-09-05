package Modelo;

import java.awt.*;
import javax.swing.*;
public class Fase1 extends JPanel {

    private Image fundoTela;

    public Fase1(){
        ImageIcon pegaimagem = new ImageIcon("src//res//ChatGPT Image 29 de ago. de 2025, 19_14_58.png");
        fundoTela = pegaimagem.getImage();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if (fundoTela == null) {
            
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Imagem do menu não encontrada", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int panelW = getWidth();
        int panelH = getHeight();

        
        int imgW = fundoTela.getWidth(this);
        int imgH = fundoTela.getHeight(this);

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
        g2.dispose();
    }
}

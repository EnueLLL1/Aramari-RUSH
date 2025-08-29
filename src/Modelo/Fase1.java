package Modelo;

import javax.swing.*;
import java.awt.*;

public class Fase1 extends JPanel {

    private Image fundoTela;

    public Fase1(){
        ImageIcon pegaimagem = new ImageIcon("src//res//ChatGPT Image 29 de ago. de 2025, 19_14_58.png");
        fundoTela = pegaimagem.getImage();
    }
    public void paint(Graphics g){
        Graphics2D graficos = (Graphics2D) g;
        graficos.drawImage(fundoTela, 0, 0, null);
        g.dispose();
    }
}

package AramariRUSH;
import Modelo.Fase1;

import javax.swing.JFrame;

public class Conteiner extends JFrame{

    public Conteiner() {
        add(new Fase1());
        setTitle("Aramari RUSH");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setResizable(true);
        setVisible(true);
    }
    public static void main (String[] args){
        new Conteiner();

    }
}

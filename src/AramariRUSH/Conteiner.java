package AramariRUSH;

import Modelo.Fase1;
import javax.swing.JFrame;

public class Conteiner extends JFrame{

    
    public Conteiner() {
        add(new Fase1());
        setSize(1920, 1080);
        setTitle("Aramari RUSH");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        Fase1 menu = new Fase1();
        setContentPane(menu);
    }
    public static void main (String[] args){

         javax.swing.SwingUtilities.invokeLater(Conteiner::new);

    }
}

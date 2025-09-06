package AramariRUSH;

import Modelo.MenuPrincipal;
import javax.swing.JFrame;

public class Container extends JFrame {

    public Container() {
        // Cria o menu principal
        MenuPrincipal menu = new MenuPrincipal();

        
        // Configura a janela
        setContentPane(menu);
        setSize(1920, 1080);
        setTitle("Diamonds For The Queen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Container();
            }
        });
    }
}
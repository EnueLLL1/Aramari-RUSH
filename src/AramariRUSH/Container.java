package AramariRUSH;

import Modelo.GameplayPanel;
import Modelo.MenuPanel;
import javax.swing.*;
import java.awt.*;

public class Container extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Container() {

        // Configura a janela
        setSize(800, 600);
        setTitle("Aramari-RUSH");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Painéis do jogo
        MenuPanel menuPanel = new MenuPanel(this);
        GameplayPanel gameplayPanel = new GameplayPanel();

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gameplayPanel, "Gameplay");

        cardLayout.show(mainPanel, "Menu");

        add(mainPanel);

        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setVisible(true);
    }
    // Método que deixa outras classes trocarem de tela
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
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
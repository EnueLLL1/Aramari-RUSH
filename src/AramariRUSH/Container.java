package AramariRUSH;

import Modelo.GameplayPanel;
import Modelo.MenuPanel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

public class Container extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameplayPanel gameplayPanel;
    private MenuPanel menuPanel;

    public Container() {
        // Configura a janela
        setSize(800, 600);
        setTitle("Aramari-RUSH");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Cria os painéis
        menuPanel = new MenuPanel(this);
        gameplayPanel = new GameplayPanel();

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gameplayPanel, "Gameplay");

        // Mostra o menu inicialmente
        cardLayout.show(mainPanel, "Menu");

        add(mainPanel);
        setResizable(true);

        // Listener para detectar mudanças de tela e dar foco apropriado
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Força o foco no painel ativo
                SwingUtilities.invokeLater(() -> {
                    Component[] components = mainPanel.getComponents();
                    for (Component comp : components) {
                        if (comp.isVisible() && comp instanceof GameplayPanel) {
                            comp.requestFocusInWindow();
                            break;
                        }
                    }
                });
            }
        });

        setVisible(true);
    }

    // Método que permite outras classes trocarem de tela
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
        
        // Força o foco no painel correto após a troca
        SwingUtilities.invokeLater(() -> {
            if ("Gameplay".equals(name)) {
                gameplayPanel.requestFocusInWindow();
                gameplayPanel.setStarted(true);
                System.out.println("Foco transferido para GameplayPanel");
            } else if ("Menu".equals(name)) {
                menuPanel.requestFocusInWindow();
                System.out.println("Foco transferido para MenuPanel");
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Container();
        });
    }
}
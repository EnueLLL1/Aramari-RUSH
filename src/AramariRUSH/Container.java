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
        // Configuração básica da janela
        setTitle("DIAMONDS FOR THE QUEEN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Layout de troca de telas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Criação dos painéis
        menuPanel = new MenuPanel(this);
        gameplayPanel = new GameplayPanel(this);

        // Define tamanhos fixos e coerentes
        menuPanel.setPreferredSize(new Dimension(800, 800));
        gameplayPanel.setPreferredSize(new Dimension(800, 800));

        // Adiciona os painéis ao container principal
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gameplayPanel, "Gameplay");
        add(mainPanel);

        // Ajusta a janela para o tamanho exato dos painéis
        pack();
        setLocationRelativeTo(null);

        // Mostra o menu inicialmente
        cardLayout.show(mainPanel, "Menu");

        // Listener para focar o painel visível (boa prática)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Component[] components = mainPanel.getComponents();
                    for (Component comp : components) {
                        if (comp.isVisible()) {
                            comp.requestFocusInWindow();
                            break;
                        }
                    }
                });
            }
        });

        setVisible(true);
    }

    // Método para alternar entre telas
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);

        SwingUtilities.invokeLater(() -> {
            if ("Gameplay".equals(name)) {
                gameplayPanel.setStarted(true);
                gameplayPanel.reiniciarJogo();
                gameplayPanel.requestFocusInWindow();
                System.out.println("→ Foco transferido para GameplayPanel");
            } else if ("Menu".equals(name)) {
                gameplayPanel.setStarted(false);
                menuPanel.requestFocusInWindow();
                System.out.println("→ Foco transferido para MenuPanel");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Container::new);
    }
}

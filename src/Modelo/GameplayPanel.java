package Modelo;

import javax.swing.*;
import java.awt.*;

public class GameplayPanel extends JPanel {
    public GameplayPanel() {
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.drawString("Suposta gameplay rodando...", 50, 50);
    }
}
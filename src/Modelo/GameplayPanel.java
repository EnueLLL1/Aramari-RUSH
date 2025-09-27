package Modelo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameplayPanel extends JPanel implements ActionListener {
    
    private Player player;
    private Timer timer;

    public GameplayPanel() {
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        player = new Player();
        player.load();

        addKeyListener(new TecladoAdapter());

        timer = new Timer(5, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        Graphics2D graficos = (Graphics2D) g;
        
        // Anti-aliasing para melhor qualidade
        graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                  RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha o player
        if(player.getImagem() != null) {
            graficos.drawImage(player.getImagem(), player.getX(), player.getY(), this);
        }
        
        // Toolkit para sincronização
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        repaint();
    }

    private class TecladoAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e);
        }
    }
}
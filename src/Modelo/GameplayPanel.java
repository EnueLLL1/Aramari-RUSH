package Modelo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameplayPanel extends JPanel implements ActionListener {
    
    private ArrayList<Projectile> projectiles;
    private Player player;
    private Timer timer;

    public GameplayPanel() {
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        player = new Player();
        player.load();
        projectiles = new ArrayList<>(); // Lista pros projeteis
        //Pra outros sprites, adicionar aqui


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
        
        // Desenha os projeteis
        for(Projectile projectile : projectiles) {
            if(projectile.getImagem() != null) {
                graficos.drawImage(projectile.getImagem(), projectile.getX(), projectile.getY(), this);
            }
        }

        // Toolkit para sincronização
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        // Update nos projetil e remove fora da tela
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();
            if (!p.isVisible()) {
                it.remove();
            }
        }

        //Adicionar aqui os sprites dos inimigos tambem

        repaint();
    }

    private class TecladoAdapter extends KeyAdapter {
        // TODO fazer o player atirar em 4 direções
        @Override
        public void keyPressed(KeyEvent e) {
           int codigo = e.getKeyCode();
            if (codigo == KeyEvent.VK_SPACE) {
                int projDx = player.getDx() != 0 ? player.getDx() : player.getLastDx();
                int projDy = player.getDy() != 0 ? player.getDy() : player.getLastDy();

                projectiles.add(new Projectile(
                    player.getX() + (player.getLargura() / 2),
                    player.getY() + (player.getAltura() / 2),
                    projDx,
                    projDy 
                ));
            } else {
                player.keyPressed(e);
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e);
        }
    }
}
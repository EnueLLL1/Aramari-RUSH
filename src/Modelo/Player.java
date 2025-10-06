package Modelo;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Player extends JPanel {
   
    private int x, y;
    private int dx, dy, pdx, pdy;
    private int lastDx, lastDy, lastPdx, lastPdy;
    private Image imagem;
    private int altura, largura;
    private final int VELOCIDADE = 3;

    public Player(){
        this.x = 400 - 25; 
        this.y = 300 - 25;
        this.pdx = 0;
        this.pdy = 0;
        this.dx = 0;
        this.dy = 0;
        this.lastDx = 0; 
        this.lastDy = 0;
        this.lastPdx = 0;
        this.lastPdy = 0;
    }

    public void load(){
       ImageIcon icon = new ImageIcon("src//res//personagem//phr1.png");
       imagem = icon.getImage();
       
       if(imagem != null) {
           altura = imagem.getHeight(null);
           largura = imagem.getWidth(null);

           this.x = 400 - (largura / 2);
           this.y = 300 - (altura / 2);
       }
    }

    public void update(){
        x += dx;
        y += dy;
        
        
        if (dx != 0) lastDx = dx;
        if (dy != 0) lastDy = dy;
        
        // Controla os limites da tela (assumindo 800x600)
        if(x < 0) x = 0;
        if(y < 0) y = 0;
        if(x > 800 - largura) x = 800 - largura;
        if(y > 600 - altura) y = 600 - altura;
    }

    public void keyPressed(KeyEvent tecla) {
        int codigo = tecla.getKeyCode();

        if(codigo == KeyEvent.VK_W){
            pdy = -VELOCIDADE;
            dy = -VELOCIDADE;
            lastDy = -VELOCIDADE; 
            lastDx = 0;
            lastPdx = 0;
            lastPdy = pdy;
        }else if(codigo == KeyEvent.VK_S){
            pdy = VELOCIDADE;
            dy = VELOCIDADE; 
            lastDy = VELOCIDADE;
            lastDx = 0;
            lastPdx = 0;
            lastPdy = pdy;
        }else if(codigo == KeyEvent.VK_A){
            pdx = -VELOCIDADE;
            dx = -VELOCIDADE;
            lastDx = -VELOCIDADE;
            lastDy = 0;
            lastPdy = 0;
            lastPdx = pdx;
        }else if(codigo == KeyEvent.VK_D){
            pdx = VELOCIDADE;
            dx = VELOCIDADE;
            lastDx = VELOCIDADE;
            lastDy = 0;
            lastPdy = 0;
            lastPdx = pdx;
        }
    }
    
    public void keyRelease(KeyEvent tecla) {
        int codigo = tecla.getKeyCode();
        if(codigo == KeyEvent.VK_W || codigo == KeyEvent.VK_S){
            dy = 0;
            
        }
        
        if(codigo == KeyEvent.VK_D || codigo == KeyEvent.VK_A){
            dx = 0;
            
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getImagem() {
        return imagem;
    }

    public void setImagem(Image imagem) {
        this.imagem = imagem;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getLastDx() {
        return lastDx;
    }

    public int getLastDy() {
        return lastDy;
    }

    public int getPdx() {
        return pdx;
    }

    public void setPdx(int pdx) {
        this.pdx = pdx;
    }

    public int getPdy() {
        return pdy;
    }

    public void setPdy(int pdy) {
        this.pdy = pdy;
    }

    public int getLastPdx() {
        return lastPdx;
    }

    public void setLastPdx(int lastPdx) {
        this.lastPdx = lastPdx;
    }

    public int getLastPdy() {
        return lastPdy;
    }

    public void setLastPdy(int lastPdy) {
        this.lastPdy = lastPdy;
    }
    
}
package Modelo;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Collectible {
    private int x, y;
    private int largura, altura;
    private Image imagem;
    private boolean visivel;
    private DiamondType type;

    public enum DiamondType {
        COMUM,
        RARO,
        LENDARIO
    }

    public Collectible(int x, int y, DiamondType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.visivel = true;
        load();
    }

    private void load() {
        String path = "";
        switch (type) {
            case COMUM:
                path = "src/res/objects/cdiamond.png";
                break;
            case RARO:
                path = "src/res/objects/rdiamond.png";
                break;
            case LENDARIO:
                path = "src/res/objects/ldiamond.png";
                break;
        }
        
        ImageIcon referencia = new ImageIcon(path);
        imagem = referencia.getImage();
        this.largura = imagem.getWidth(null);
        this.altura = imagem.getHeight(null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, largura, altura);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImagem() {
        return imagem;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public DiamondType getType() {
        return type;
    }
}
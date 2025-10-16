package Modelo.Entidades;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Collectible extends Entity {

    private DiamondType type;
    private int value;
    private Image image;

    public enum DiamondType {
        COMUM,
        RARO,
        LENDARIO
    }

    public Collectible(int x, int y, DiamondType type) {
        super(x, y, 0);
        this.type = type;
        setValue();
        load();
    }

    private void setValue() {
        switch (type) {
            case COMUM:
                value = 10;
                break;
            case RARO:
                value = 50;
                break;
            case LENDARIO:
                value = 100;
                break;
        }
    }

    @Override
    public void load() {
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

        try {
            ImageIcon icon = new ImageIcon(path);
            image = icon.getImage();
            if (image != null) {
                width = image.getWidth(null);
                height = image.getHeight(null);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar diamante: " + path);
            width = 16;
            height = 16;
        }
    }

    @Override
    public void update() {
    }

    public DiamondType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public boolean isVisivel() {
        return visible;
    }

    public void setVisivel(boolean visible) {
        this.visible = visible;
    }

    public Image getImagem() {
        return image;
    }
}
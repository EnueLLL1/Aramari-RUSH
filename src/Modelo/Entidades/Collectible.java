package Modelo.Entidades;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Collectible extends Entity {

    private DiamondType type;
    private int value;
    private Image image;

    public enum DiamondType {
        COMUM(10, "src/res/objects/cdiamond.png"),
        RARO(50, "src/res/objects/rdiamond.png"),
        LENDARIO(100, "src/res/objects/ldiamond.png");

        private final int defaultValue;
        private final String imagePath;

        DiamondType(int defaultValue, String imagePath) {
            this.defaultValue = defaultValue;
            this.imagePath = imagePath;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    private Collectible(CollectibleBuilder builder) {
        super(builder.x, builder.y, 0);
        this.type = builder.type;
        this.value = builder.value;
        load();
    }

    @Override
    public void load() {
        String path = type.getImagePath();

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
        // Collectibles são estáticos
    }

    // ========================================
    // GETTERS
    // ========================================
    
    public DiamondType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public boolean isVisivel() {
        return visible;
    }

    public Image getImagem() {
        return image;
    }

    // ========================================
    // SETTERS
    // ========================================
    
    public void setVisivel(boolean visible) {
        this.visible = visible;
    }

    // ========================================
    // BUILDER PATTERN
    // ========================================
    
    public static class CollectibleBuilder {
        private final int x;
        private final int y;
        private final DiamondType type;
        
        private int value;

        public CollectibleBuilder(int x, int y, DiamondType type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.value = type.getDefaultValue();
        }

        public CollectibleBuilder value(int value) {
            this.value = value;
            return this;
        }

        public Collectible build() {
            return new Collectible(this);
        }
    }
}
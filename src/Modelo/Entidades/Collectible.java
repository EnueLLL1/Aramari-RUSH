package Modelo.Entidades;

import java.awt.Graphics2D;

import javax.imageio.ImageIO;

public class Collectible extends Entity {

    private final DiamondType type;
    private final int value;

    public enum DiamondType {
        COMUM(10, "/res/objects/cdiamond.png"),
        RARO(50, "/res/objects/rdiamond.png"),
        LENDARIO(100, "/res/objects/ldiamond.png");

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
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream(type.getImagePath()));
            if (sprite != null) {
                width = sprite.getWidth();
                height = sprite.getHeight();
            } else {
                System.err.println("❌ Erro: sprite do diamante não encontrado: " + type.getImagePath());
                width = 16;
                height = 16;
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar diamante: " + e.getMessage());
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

    @Override
    public void draw(Graphics2D g2) {
        if (!visible || sprite == null) return;
        g2.drawImage(sprite, x, y, width, height, null);
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
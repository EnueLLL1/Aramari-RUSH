package Modelo.Entidades;

import javax.imageio.ImageIO;

public class PowerUp extends Entity {

    private PowerUpType type;

    public enum PowerUpType {
        TRIPLE_SHOT("/res/objects/shotgun1.png", 15000); // 15 segundos em ms

        private final String imagePath;
        private final int duration;

        PowerUpType(String imagePath, int duration) {
            this.imagePath = imagePath;
            this.duration = duration;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getDuration() {
            return duration;
        }
    }

    private PowerUp(PowerUpBuilder builder) {
        super(builder.x, builder.y, 0);
        this.type = builder.type;
        load();
    }

    @Override
    public void load() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream(type.getImagePath()));
            if (sprite == null) {
                System.err.println("❌ Erro: sprite do power-up não encontrado!");
            }
            width = sprite != null ? sprite.getWidth() : 16;
            height = sprite != null ? sprite.getHeight() : 16;
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar sprite do power-up: " + e.getMessage());
            width = 16;
            height = 16;
        }
    }

    @Override
    public void update() {
        // Power-ups são estáticos
    }

    // ========================================
    // GETTERS
    // ========================================
    
    public PowerUpType getType() {
        return type;
    }

    public int getDuration() {
        return type.getDuration();
    }

    // ========================================
    // BUILDER PATTERN
    // ========================================
    
    public static class PowerUpBuilder {
        private final int x;
        private final int y;
        private final PowerUpType type;

        public PowerUpBuilder(int x, int y, PowerUpType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public PowerUp build() {
            return new PowerUp(this);
        }
    }
}
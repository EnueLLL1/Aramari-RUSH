package Modelo.Entidades;

public class PowerUpFactory {
    
    /**
     * Cria um power-up de tiro triplo
     * @param x Posição X inicial
     * @param y Posição Y inicial
     * @return PowerUp configurado
     */
    public static PowerUp createTripleShotPowerUp(int x, int y) {
        return new PowerUp.PowerUpBuilder(x, y, PowerUp.PowerUpType.TRIPLE_SHOT)
                .build();
    }
}
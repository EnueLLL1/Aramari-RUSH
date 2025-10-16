package Modelo.Entidades;

public class EnemyFactory {

    /**
     * Cria um inimigo básico do Tipo 1
     * - Vida baixa
     * - Velocidade média
     * - Dano baixo
     */
    public static Enemy createBasicEnemy(int x, int y) {
        return new Enemy.EnemyBuilder(x, y, Enemy.EnemyType.TIPO1)
                .health(50)
                .damage(5)
                .speed(2)
                .score(50)
                .build();
    }

    /**
     * Cria um inimigo forte do Tipo 2
     * - Vida alta
     * - Velocidade baixa
     * - Dano alto
     */
    public static Enemy createStrongEnemy(int x, int y) {
        return new Enemy.EnemyBuilder(x, y, Enemy.EnemyType.TIPO2)
                .health(150)
                .damage(20)
                .speed(1)
                .score(150)
                .build();
    }

    /**
     * Cria um inimigo rápido do Tipo 1
     * - Vida muito baixa
     * - Velocidade alta
     * - Dano médio
     */
    public static Enemy createFastEnemy(int x, int y) {
        return new Enemy.EnemyBuilder(x, y, Enemy.EnemyType.TIPO1)
                .health(30)
                .damage(10)
                .speed(2)
                .score(75)
                .build();
    }

    /**
     * Cria um inimigo tanque do Tipo 2
     * - Vida muito alta
     * - Velocidade muito baixa
     * - Dano médio
     */
    public static Enemy createTankEnemy(int x, int y) {
        return new Enemy.EnemyBuilder(x, y, Enemy.EnemyType.TIPO2)
                .health(300)
                .damage(15)
                .speed(1)
                .score(75)
                .build();
    }

    /**
     * Cria um inimigo aleatório baseado em probabilidade
     */
    public static Enemy createRandomEnemy(int x, int y) {
        int random = (int) (Math.random() * 100);

        if (random < 50) {
            // 50% chance - Inimigo básico
            return createBasicEnemy(x, y);
        } else if (random < 75) {
            // 25% chance - Inimigo rápido
            return createFastEnemy(x, y);
        } else if (random < 90) {
            // 15% chance - Inimigo forte
            return createStrongEnemy(x, y);
        } else {
            // 10% chance - Inimigo tanque
            return createTankEnemy(x, y);
        }
    }
}

package Modelo;

public class CommonScoreStrategy implements ScoreStrategy {
     @Override
    public int calculateScore(Collectible c) {
        switch (c.getType()) {
            case COMUM:
                return 100;
            case RARO:
                return 250;
            case LENDARIO:
                return 500;
            default:
                return 0;
        }
    }
}

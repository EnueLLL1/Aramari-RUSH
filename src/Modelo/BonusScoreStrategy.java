package Modelo;

public class BonusScoreStrategy implements ScoreStrategy {
    @Override
    public int calculateScore(Collectible c) {
        switch (c.getType()) {
            case COMUM:
                return 200;
            case RARO:
                return 500;
            case LENDARIO:
                return 1000;
            default:
                return 0;
        }
    }
}

package Modelo.UI;

import java.util.Random;

public class ScreenShake {
    
    private int shakeIntensity;
    private int shakeDuration;
    private int shakeTimer;
    private Random random;
    
    private int offsetX;
    private int offsetY;
    
    public ScreenShake() {
        this.random = new Random();
        this.shakeIntensity = 0;
        this.shakeDuration = 0;
        this.shakeTimer = 0;
        this.offsetX = 0;
        this.offsetY = 0;
    }
    
    /**
     * Inicia o efeito de tremor de tela
     * @param intensity Intensidade do tremor (pixels de deslocamento máximo)
     * @param duration Duração do tremor em frames
     */
    public void startShake(int intensity, int duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeTimer = duration;
    }
  
    public void update() {
        if (shakeTimer > 0) {
            shakeTimer--;
            
            // Calcula o progresso (1.0 = início, 0.0 = fim)
            float progress = (float) shakeTimer / shakeDuration;
            int currentIntensity = (int) (shakeIntensity * progress);
            
            // Gera offsets aleatórios baseados na intensidade atual
            offsetX = random.nextInt(currentIntensity * 2 + 1) - currentIntensity;
            offsetY = random.nextInt(currentIntensity * 2 + 1) - currentIntensity;
            
            // Quando o timer termina, reseta os offsets
            if (shakeTimer == 0) {
                offsetX = 0;
                offsetY = 0;
            }
        }
    }
    
    public int getOffsetX() {
        return offsetX;
    }
    
    public int getOffsetY() {
        return offsetY;
    }
    
    public boolean isShaking() {
        return shakeTimer > 0;
    }
    
    
    public void stop() {
        shakeTimer = 0;
        offsetX = 0;
        offsetY = 0;
    }
}
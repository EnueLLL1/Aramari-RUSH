package Modelo.UI;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Gerenciador centralizado de fontes do jogo.
 * Carrega e fornece acesso à fonte customizada "runescape_uf" em todo o projeto.
 */
public class FontManager {
    
    private static FontManager instance;
    private Font customFont;
    private Font fallbackFont;
    
    private FontManager() {
        loadCustomFont();
    }
    
    /**
     * Retorna a instância singleton do FontManager
     */
    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }
    
    /**
     * Carrega a fonte customizada do arquivo TTF
     */
    private void loadCustomFont() {
        try {
            // Carrega a fonte do classpath
            InputStream is = getClass().getResourceAsStream("/res/font/AncientModernTales.ttf");
            
            if (is == null) {
                throw new FontFormatException("Arquivo de fonte não encontrado no classpath.");
            }
            
            // Cria a fonte a partir do InputStream
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            
            // Registra a fonte no ambiente gráfico local
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            
            System.out.println("✅ Fonte 'AncientModernTales.ttf' carregada com sucesso!");
            
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao carregar a fonte customizada: " + e.getMessage());
            customFont = null;
        }
        
        // Define fonte de fallback
        fallbackFont = new Font("Monospaced", Font.PLAIN, 12);
    }
    
    /**
     * Retorna a fonte customizada ou a fonte de fallback se não estiver disponível
     */
    public Font getCustomFont() {
        return customFont != null ? customFont : fallbackFont;
    }
    
    /**
     * Retorna a fonte customizada com tamanho específico
     */
    public Font getCustomFont(int size) {
        if (customFont != null) {
            return customFont.deriveFont(Font.PLAIN, size);
        }
        return fallbackFont.deriveFont(Font.PLAIN, size);
    }
    
    /**
     * Retorna a fonte customizada com estilo e tamanho específicos
     */
    public Font getCustomFont(int style, int size) {
        if (customFont != null) {
            return customFont.deriveFont(style, size);
        }
        return fallbackFont.deriveFont(style, size);
    }
    
    /**
     * Verifica se a fonte customizada está disponível
     */
    public boolean isCustomFontAvailable() {
        return customFont != null;
    }
    
    /**
     * Retorna a fonte de fallback
     */
    public Font getFallbackFont() {
        return fallbackFont;
    }
}

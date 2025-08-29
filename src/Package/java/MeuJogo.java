package Package.java;
import javax.swing.JFrame;

public class MeuJogo extends JFrame{

    public MeuJogo () {
        setTitle("Aramari RUSH");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setResizable(true);
        setVisible(true);
    }
    public static void main (String[] args){
    new MeuJogo();

    }
}

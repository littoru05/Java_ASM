import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("Galaxy Shooter");
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        GamePanel game = new GamePanel();
        
        mainPanel.add(game, "GAME");

        window.add(mainPanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        cardLayout.show(mainPanel, "HOME");
    }
}                   
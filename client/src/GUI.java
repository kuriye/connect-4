import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{

    JPanel content;

    public GUI() throws HeadlessException {
        this.content = new JPanel(new BorderLayout());

        GridPanel panel = new GridPanel(6,7);

        this.content.add(panel, BorderLayout.CENTER);


        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setContentPane(content);
        this.setVisible(true);
    }
}

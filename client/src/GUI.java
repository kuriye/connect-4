import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{

    JPanel content;

    public GUI() throws HeadlessException {
        this.content = new JPanel(new BorderLayout());


        this.setSize(getWidth(), getHeight());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setContentPane(content);
        this.setVisible(true);
    }
}

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GridPanel extends JPanel implements MouseListener {
    private int rows;
    private int cols;

    private ArrayList<CoinLocation> coins = new ArrayList<>();

    public GridPanel(int rows, int cols){
        this.rows = rows;
        this.cols = cols;


    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

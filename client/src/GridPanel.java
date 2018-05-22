import javax.swing.*;
import java.awt.*;
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

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                coins.add(new CoinLocation(getWidth()/12*2 + 50 + 70*i, getHeight()/14+ 50 * 70 *j,50 ,50));
            }
        }

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(0, 102, 255));
        g2d.fillRoundRect(getWidth()/12*2, getHeight()/14, getWidth()/12*8, (int) (getHeight()/8*6.5), 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(getWidth()/12*2, getHeight()/14, getWidth()/12*8, (int) (getHeight()/8*6.5), 15, 15);
        g2d.setColor(new Color(0, 102, 255));
        g2d.fillRoundRect(getWidth()/12*2, (int) (getHeight()/8*6.5), 50, getHeight()-(int) (getHeight()/8*6.5), 15, 15);
        g2d.fillRoundRect(getWidth()/12*10 - 50, (int) (getHeight()/8*6.5), 50, getHeight()-(int) (getHeight()/8*6.5), 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(getWidth()/12*2, (int) (getHeight()/8*6.5), 50, getHeight()-(int) (getHeight()/8*6.5), 15, 15);
        g2d.drawRoundRect(getWidth()/12*10 -50, (int) (getHeight()/8*6.5), 50, getHeight()-(int) (getHeight()/8*6.5), 15, 15);

        for(CoinLocation coin: coins){
            coin.draw(g2d);
        }

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

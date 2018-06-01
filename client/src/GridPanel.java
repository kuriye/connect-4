import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class GridPanel extends JPanel  {
    private int rows;
    private int cols;
    private Graphics2D g;
    private boolean makeButtons = true;

    private ArrayList<CoinLocation> coins = new ArrayList<>();
    private ArrayList<Point2D> buttons = new ArrayList<>();

    public GridPanel(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        this.g = g2d;

        Rectangle2D.Double scherm = new Rectangle2D.Double(0,0,getWidth(), getHeight());

        //background
        g2d.setColor(new Color(255,255,255));
        g2d.fill(scherm);

       // g2d.drawString("Your Turn");

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

        makeCoins();

        for(int i = 0; i < rows; i++){
            g2d.setColor(new Color(41, 163, 41));
            g2d.fillOval(getWidth()/12*2 + 130 + 160*i, getHeight()/14 - 70,60 ,60);
            g2d.setColor(new Color(71, 209, 71));
            g2d.fillOval(getWidth()/12*2 + 135 + 160*i, getHeight()/14 - 68,50 ,50);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(getWidth()/12*2 + 135 + 160*i, getHeight()/14 - 68,50 ,50);
            g2d.drawOval(getWidth()/12*2 + 130 + 160*i, getHeight()/14 - 70,60 ,60);
            if(makeButtons)
                 buttons.add(new Point2D.Double(getWidth()/12*2 + 135 + 160*i,getHeight()/14 - 68));
        }
        makeButtons = false;

        for(CoinLocation coin : coins){
            coin.draw();
        }
    }

    public ArrayList<Point2D> getButtons(){
        return buttons;
    }

    public ArrayList<CoinLocation> getCoins(){
        return coins;
    }

    private void makeCoins(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                coins.add(new CoinLocation(getWidth()/12*2 + 110 + 160*i, getHeight()/14 + 50 + 120 *j,100 ,100, i, j, g));
            }
        }
    }
}

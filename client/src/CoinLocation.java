import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Comparator;

public class CoinLocation implements Comparable<CoinLocation>, Comparator<CoinLocation>{
    private Ellipse2D.Double circle;
    private Color color;
    private int row;
    private int column;
    private Graphics2D g;
    private boolean isDrawnWithColor;

    public CoinLocation(int x, int y, int width, int height, int row, int column, Graphics2D g){
        this.circle = new Ellipse2D.Double(x, y, width, height);
        this.color = Color.white;
        this.row = row;
        this.column = column;
        this.g = g;
        this.isDrawnWithColor = false;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Point getPoint() {
        return new Point(this.getX(), this.getY());
    }

    public int getX() {
        return (int)this.circle.getX();
    }

    public int getY() {
        return (int)this.circle.getY();
    }

    private int getWidth() {
        return (int)this.circle.getWidth();
    }

    private int getHeight() {
        return (int)this.circle.getHeight();
    }

    public void draw() {
        g.setColor(this.color);
        g.fillOval(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.drawOval(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(Color.white);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isDrawnWithColor() {
        return isDrawnWithColor;
    }

    public void setDrawnWithColor(boolean drawnWithColor) {
        isDrawnWithColor = drawnWithColor;
    }

    public void setGraphics(Graphics2D g) {
        this.g = g;
    }

    public static Comparator<CoinLocation> compareColumn(){
        return Comparator.comparingInt(CoinLocation::getColumn);
    }

    @Override
    public String toString(){
        return "(Column: " + column + " - Row: " + row + ")";
    }

    @Override
    public int compare(CoinLocation coin1, CoinLocation coin2) {
        return coin1.compareTo(coin2);
    }

    @Override
    public boolean equals(Object other) {
        boolean same = false;
        if(other instanceof CoinLocation){
            CoinLocation otherCoin = (CoinLocation) other;
            same = row == otherCoin.getRow() && column == otherCoin.getColumn();
        }
        return same;
    }

    @Override
    public int compareTo(CoinLocation coin) {
        if(column > coin.getColumn()){
            return 1;
        }else if(column < coin.getColumn()){
            return -1;
        }else if(row > coin.getRow()){
            return 1;
        }else if(row < coin.getRow()){
            return -1;
        }else{
            return 0;
        }
    }
}

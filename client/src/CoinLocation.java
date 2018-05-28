import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class CoinLocation {
    private Ellipse2D.Double circle;
    private Color color;
    private int row;
    private int column;
    private Graphics2D g;

    public CoinLocation(int x, int y, int width, int height, int row, int column, Graphics2D g){
        this.circle = new Ellipse2D.Double(x, y, width, height);
        this.color = Color.white;
        this.row = row;
        this.column = column;
        this.g = g;
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
        g.setColor(Color.white);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}

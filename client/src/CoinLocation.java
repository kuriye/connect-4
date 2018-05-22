import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CoinLocation {
    private Ellipse2D.Double circle;
    private Color color;

    public CoinLocation(int x, int y, int width, int height){
        this.circle = new Ellipse2D.Double(x, y, width, height);
        this.color = new Color(255, 255,255);
    }

    private void setColor(Color color){
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

    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillOval(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(new Color(51, 51, 51));
    }
}

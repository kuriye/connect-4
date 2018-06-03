import java.awt.*;

public class StatusBox {
    private Graphics2D g;
    private String text;
    private int x;
    private int y;

    public StatusBox(Graphics2D g) {
        this.g = g;
        this.x = 36;
        this.y = 36;
    }

    public void draw(Graphics2D g2d){
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.3F);
        g2d.setFont(newFont);
        g2d.setColor(new Color(99, 152, 237));
        g2d.fillRoundRect(15, 15, 400, 30, 12,12);
        g2d.setColor(Color.black);
        g2d.drawRoundRect(15, 15, 400, 30, 12,12);
        g2d.drawString("status: " + text, x, y);
    }

    public void setText(String text) {
        this.text = text;
    }
}

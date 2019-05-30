import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel {
    MyPanel() {

    }

    public void paintComponent(Graphics g) {
        if (Main.imag == null) {
            Main.imag = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D d2 = Main.imag.createGraphics();
            d2.setColor(Color.white);
            d2.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        super.paintComponent(g);
        g.drawImage(Main.imag, 0, 0, this);
    }
}
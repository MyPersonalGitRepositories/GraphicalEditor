import javax.swing.*;

public class ColorDialog extends JDialog {
    public ColorDialog(JFrame owner, String title) {
        super(owner, title, true);
        add(Main.tcc);
        setSize(200, 200);
    }
}
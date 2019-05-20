import javax.swing.*;

class ColorDialog extends JDialog {
    ColorDialog(JFrame owner, String title) {
        super(owner, title, true);
        add(Main.tcc);
        setSize(200, 200);
    }
}
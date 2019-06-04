import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    private static int x, y;
    // Режим рисования 
    private static int rezhim = 0;
    private static int xPad;
    private static int xf;
    private static int yf;
    private static int yPad;
    private static boolean pressed = false;
    private JSlider slider;
    // текущий цвет
    private static Color maincolor;
    private static MyFrame frame;
    private static MyPanel panel;
    private static JButton colorbutton;
    static JColorChooser tcc;
    // поверхность рисования
    static BufferedImage imag;
    // если мы загружаем картинку
    private static boolean loading = false;
    private static String fileName;

    private Main() {

        frame = new MyFrame("Графічний редактор");
        frame.setSize(Preferences.FRAME_WIDTH, Preferences.FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setResizable(false);
        maincolor = Color.black;

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenu infoMenu = new JMenu("Довідка");
        menuBar.add(infoMenu);

        Action helpAction = new AbstractAction("Допомога") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Якщо у вас виникли будь-які проблеми з роботою програми \"GraphicalEditor\" \n" +
                        " ви можете звернутися за допомогою до її автора: Підлісного Максима" +
                        "  \n за контактими: \n" + "\n Почта: p.maxsym@gmail.com " + "\n Телефон: +1234567890 \n");
            }
        };

        Action infoAction = new AbstractAction("Ліцензія") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Програмне забезпечення: \"GraphicalEditor\"\nАвтор: Підлісний Максим Віталійович\n\n                   " +
                        "                                                                     " +
                        "ЛІЦЕНЗІЯ\nЦією Ліцензією засвідчується право ЛІЦЕНЗІАТА на використання програмного забезпечення GraphicalEditor.\n ");
            }
        };

        Action exitAction = new AbstractAction("Вийти") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Ви дійсно впевнені, що хочете закрити вікно програми?", "Вихід", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };

        Action loadAction = new AbstractAction("Загрузити") {

            public void actionPerformed(ActionEvent event) {

                JFileChooser jf = new JFileChooser();
                int result = jf.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {

                    try {

                        // при выборе изображения подстраиваем размеры формы
                        // и панели под размеры данного изображения
                        fileName = jf.getSelectedFile().getAbsolutePath();
                        File iF = new File(fileName);
                        jf.addChoosableFileFilter(new TextFileFilter(".png"));
                        jf.addChoosableFileFilter(new TextFileFilter(".jpg"));
                        imag = ImageIO.read(iF);
                        int w = imag.getWidth();
                        int h = imag.getHeight();
                        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        AffineTransform at = new AffineTransform();
                        at.scale(2.0, 2.0);
                        AffineTransformOp scaleOp =
                                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                        after = scaleOp.filter(imag, after);
                        loading = true;
//                        frame.setSize(panel.getWidth(), panel.getWidth());
//                        panel.setSize(imag.getWidth(), imag.getWidth());
                        panel.repaint();
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(frame, "Такого файлу не існує");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Випадок ввода-вивода");
                    } catch (Exception ignored) {
                    }
                }
            }
        };

        JMenuItem loadMenu = new JMenuItem(loadAction);
        JMenuItem exitMenu = new JMenuItem(exitAction);

        JMenuItem licenceMenu = new JMenuItem(infoAction);
        JMenuItem helpMenu = new JMenuItem(helpAction);

        infoMenu.add(licenceMenu);
        infoMenu.add(helpMenu);

        fileMenu.add(exitMenu);
        fileMenu.add(loadMenu);

        Action saveAction = new AbstractAction("Зберегти") {

            public void actionPerformed(ActionEvent event) {

                try {

                    JFileChooser jf = new JFileChooser();
                    // Создаем фильтры  файлов
                    TextFileFilter pngFilter = new TextFileFilter(".png");
                    TextFileFilter jpgFilter = new TextFileFilter(".jpg");

                    if (fileName == null) {

                        // Добавляем фильтры
                        jf.addChoosableFileFilter(pngFilter);
                        jf.addChoosableFileFilter(jpgFilter);
                        int result = jf.showSaveDialog(null);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            fileName = jf.getSelectedFile().getAbsolutePath();
                        }
                    }
                    // Смотрим какой фильтр выбран
                    if (jf.getFileFilter() == pngFilter) {
                        ImageIO.write(imag, "png", new File(fileName + ".png"));
                    } else {
                        ImageIO.write(imag, "jpeg", new File(fileName + ".jpg"));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Помилка ввода-вивода");
                }
            }
        };

        JMenuItem saveMenu = new JMenuItem(saveAction);
        fileMenu.add(saveMenu);

        Action saveAsAction = new AbstractAction("Зберегти як...") {

            public void actionPerformed(ActionEvent event) {

                try {

                    JFileChooser jf = new JFileChooser();
                    // Создаем фильтры для файлов
                    TextFileFilter pngFilter = new TextFileFilter(".png");
                    TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                    // Добавляем фильтры
                    jf.addChoosableFileFilter(pngFilter);
                    jf.addChoosableFileFilter(jpgFilter);
                    int result = jf.showSaveDialog(null);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        fileName = jf.getSelectedFile().getAbsolutePath();
                    }
                    // Смотрим какой фильтр выбран
                    if (jf.getFileFilter() == pngFilter) {
                        ImageIO.write(imag, "png", new File(fileName + ".png"));
                    } else {
                        ImageIO.write(imag, "jpeg", new File(fileName + ".jpg"));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Помилка ввода-вивода");
                }
            }
        };

        JMenuItem saveasMenu = new JMenuItem(saveAsAction);
        fileMenu.add(saveasMenu);

        panel = new MyPanel();
        panel.setBackground(Color.white);
        panel.setOpaque(true);
        frame.add(panel, BorderLayout.CENTER);

        JPanel toolbar = new JPanel();
        toolbar.setPreferredSize(Preferences.TOOLBAR_DIMENSION);

        JButton penButton = new JButton(new ImageIcon("pen.png"));
        penButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        penButton.addActionListener(event -> rezhim = 0);
        toolbar.add(penButton);

        JButton brushButton = new JButton(new ImageIcon("brush.png"));
        brushButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        brushButton.addActionListener(event -> rezhim = 1);
        toolbar.add(brushButton);

        JButton zalivkaButton = new JButton(new ImageIcon("zalivka.png"));
        zalivkaButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        zalivkaButton.addActionListener(event -> rezhim = 7);
        toolbar.add(zalivkaButton);

        JButton lasticButton = new JButton(new ImageIcon("lastic.png"));
        lasticButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        lasticButton.addActionListener(event -> rezhim = 2);
        toolbar.add(lasticButton);

        JButton textButton = new JButton(new ImageIcon("text.png"));
        textButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        textButton.addActionListener(event -> rezhim = 3);
        toolbar.add(textButton);

        JButton lineButton = new JButton(new ImageIcon("line.png"));
        lineButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        lineButton.addActionListener(event -> rezhim = 4);
        toolbar.add(lineButton);

        JButton elipsButton = new JButton(new ImageIcon("elips.png"));
        elipsButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        elipsButton.addActionListener(event -> rezhim = 5);
        toolbar.add(elipsButton);

        JButton rectButton = new JButton(new ImageIcon("rect.png"));
        rectButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        rectButton.addActionListener(event -> rezhim = 6);
        toolbar.add(rectButton);

        JButton roundedRect = new JButton(new ImageIcon("roundedRect.png"));
        roundedRect.setPreferredSize(Preferences.BUTTON_DIMENSION);
        roundedRect.addActionListener(event -> rezhim = 11);
        toolbar.add(roundedRect);

        JButton filledElips = new JButton(new ImageIcon("filledElips.png"));
        filledElips.setPreferredSize(Preferences.BUTTON_DIMENSION);
        filledElips.addActionListener(event -> rezhim = 9);
        toolbar.add(filledElips);

        JButton filledRect = new JButton(new ImageIcon("filledRect.png"));
        filledRect.setPreferredSize(Preferences.BUTTON_DIMENSION);
        filledRect.addActionListener(event -> rezhim = 10);
        toolbar.add(filledRect);

        JButton roundedFilledRect = new JButton(new ImageIcon("roundedFilledRect.png"));
        roundedFilledRect.setPreferredSize(Preferences.BUTTON_DIMENSION);
        roundedFilledRect.addActionListener(event -> rezhim = 12);
        toolbar.add(roundedFilledRect);

        JButton cancelButton = new JButton(new ImageIcon("cross.png"));
        cancelButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        cancelButton.addActionListener(event -> rezhim = 8);
        toolbar.add(cancelButton);

        frame.add(toolbar, BorderLayout.WEST);

        // Тулбар для кнопок
        JPanel colorbar = new JPanel();
        colorbutton = new JButton();
        colorbar.setPreferredSize(Preferences.TOOLBAR_DIMENSION);
        colorbutton.setBackground(maincolor);
        colorbutton.setPreferredSize(Preferences.MAIN_BUTTON_DIMENSION);
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 6);
        colorbutton.setBorder(border);
        colorbutton.addActionListener(event -> {
            ColorDialog coldi = new ColorDialog(frame, "Вибір кольору");
            coldi.setVisible(true);
        });
        colorbar.add(colorbutton, BorderLayout.WEST);

        JButton redbutton = new JButton();
        redbutton.setBackground(Color.red);
        redbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        redbutton.addActionListener(event -> {
            maincolor = Color.red;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(redbutton, BorderLayout.EAST);

        JButton orangebutton = new JButton();
        orangebutton.setBackground(Color.orange);
        orangebutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        orangebutton.addActionListener(event -> {
            maincolor = Color.orange;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(orangebutton, BorderLayout.EAST);

        JButton yellowbutton = new JButton();
        yellowbutton.setBackground(Color.yellow);
        yellowbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        yellowbutton.addActionListener(event -> {
            maincolor = Color.yellow;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(yellowbutton, BorderLayout.EAST);

        JButton greenbutton = new JButton();
        greenbutton.setBackground(Color.green);
        greenbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        greenbutton.addActionListener(event -> {
            maincolor = Color.green;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(greenbutton, BorderLayout.EAST);

        JButton bluebutton = new JButton();
        bluebutton.setBackground(Color.blue);
        bluebutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        bluebutton.addActionListener(event -> {
            maincolor = Color.blue;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(bluebutton, BorderLayout.EAST);

        JButton cyanbutton = new JButton();
        cyanbutton.setBackground(Color.cyan);
        cyanbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        cyanbutton.addActionListener(event -> {
            maincolor = Color.cyan;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(cyanbutton, BorderLayout.EAST);

        JButton magentabutton = new JButton();
        magentabutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        magentabutton.setBackground(Color.magenta);
        magentabutton.addActionListener(event -> {
            maincolor = Color.magenta;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(magentabutton, BorderLayout.EAST);

        JButton whitebutton = new JButton();
        whitebutton.setBackground(Color.white);
        whitebutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        whitebutton.addActionListener(event -> {
            maincolor = Color.white;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(whitebutton, BorderLayout.EAST);

        JButton blackbutton = new JButton();
        blackbutton.setBackground(Color.black);
        blackbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        blackbutton.addActionListener(event -> {
            maincolor = Color.black;
            colorbutton.setBackground(maincolor);
        });
        colorbar.add(blackbutton, BorderLayout.EAST);
//        colorbar.setLayout(null);
        frame.add(colorbar, BorderLayout.NORTH);

        tcc = new JColorChooser(maincolor);
        tcc.getSelectionModel().addChangeListener(e -> {
            maincolor = tcc.getColor();
            colorbutton.setBackground(maincolor);
        });

        slider = new JSlider(2, 100, 2);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        panel.add(slider, BorderLayout.NORTH);

        panel.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e) {

                if (pressed) {

                    Graphics g = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(maincolor);

                    switch (rezhim) {
                        // олівець
                        case 0:
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // кисть
                        case 1:
                            g2.setStroke(new BasicStroke(slider.getValue()));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // ластик
                        case 2:
                            g2.setStroke(new BasicStroke(slider.getValue()));
                            g2.setColor(Color.WHITE);
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // заливка
                        case 7:
                            g2.setStroke(new BasicStroke(10000.0f));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        //удаление
                        case 8:
                            if (JOptionPane.showConfirmDialog(null, "Ви дійсно впевнені, що хочете все видалити?", "Видалення", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                g2.setStroke(new BasicStroke(10000.0f));
                                g2.setColor(Color.white);
                                g2.drawLine(xPad, yPad, e.getX(), e.getY());
                                break;
                            }
                            break;
                    }
                    xPad = e.getX();
                    yPad = e.getY();
                }
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                Graphics g = imag.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(maincolor);

                switch (rezhim) {
                    // карандаш
                    case 0:
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // кисть
                    case 1:
                        g2.setStroke(new BasicStroke(slider.getValue()));
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // ластик
                    case 2:
                        g2.setStroke(new BasicStroke(slider.getValue()));
                        g2.setColor(Color.WHITE);
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // текст
                    case 3:
                        // устанавливаем фокус для панели,
                        // чтобы печатать на ней текст
                        panel.requestFocus();
                        break;
                    //заливка
                    case 7:
                        g2.setStroke(new BasicStroke(10000.0f));
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    //удаление
                    case 8:
                        if (JOptionPane.showConfirmDialog(null, "Ви дійсно впевнені, що хочете все видалити?", "Видалення", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            g2.setStroke(new BasicStroke(10000.0f));
                            g2.setColor(Color.white);
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        }
                        break;
                }
                xPad = e.getX();
                yPad = e.getY();

                pressed = true;
                panel.repaint();
            }
            public void mousePressed(MouseEvent e) {
                xPad = e.getX();
                yPad = e.getY();
                xf = e.getX();
                yf = e.getY();
                pressed = true;
            }
            public void mouseReleased(MouseEvent e) {

                Graphics g = imag.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(maincolor);
                // Общие рассчеты для овала и прямоугольника
                int x1 = xf, x2 = xPad, y1 = yf, y2 = yPad;
                if (xf > xPad) {
                    x2 = xf;
                    x1 = xPad;
                }
                if (yf > yPad) {
                    y2 = yf;
                    y1 = yPad;
                }
                switch (rezhim) {
                    // линия
                    case 4:
                        g.drawLine(xf, yf, e.getX(), e.getY());
                        break;
                    // круг
                    case 5:
                        g.drawOval(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                    // прямоугольник
                    case 6:
                        g.drawRect(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                    case 9:
                        g.drawOval(x1, y1, (x2 - x1), (y2 - y1));
                        g.fillOval(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                    //заполненый прямоугольник
                    case 10:
                        g.drawRect(x1, y1, (x2 - x1), (y2 - y1));
                        g.fillRect(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                    case 11:
                        g.drawRoundRect(x1, y1, (x2 - x1), (y2 - y1), 20, 20);
                        break;
                    case 12:
                        g.drawRoundRect(x1, y1, (x2 - x1), (y2 - y1), 20, 20);
                        g.fillRoundRect(x1, y1, (x2 - x1), (y2 - y1), 20, 20);
                        break;
                }
                xf = 0;
                yf = 0;
                pressed = false;
                panel.repaint();
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            public void keyRelрeased(KeyEvent e) {
                // устанавливаем фокус для панели,
                // чтобы печатать на ней текст
                panel.requestFocus();
            }

            public void keyTyped(KeyEvent e) {
                if (rezhim == 3) {
                    Graphics g = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(maincolor);
                    g2.setStroke(new BasicStroke(slider.getValue() - ((slider.getValue() / 5))));

                    String str = "";
                    str += e.getKeyChar();
                    g2.setFont(new Font("Comic Sans MS", 0, slider.getValue()));
                    g2.drawString(str, xPad, yPad);
                    xPad += slider.getValue() - ((slider.getValue() / 3.3));
                    // устанавливаем фокус для панели,
                    // чтобы печатать на ней текст
                    panel.requestFocus();
                    panel.repaint();
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                // если делаем загрузку, то изменение размеров формы
                // отрабатываем в коде загрузки

                if (!loading) {
                    panel.setSize(frame.getWidth() - 40, frame.getHeight() - 80);
                    BufferedImage tempImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D d2 = tempImage.createGraphics();
                    d2.setColor(Color.white);
                    d2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
                    tempImage.setData(imag.getRaster());
                    imag = tempImage;
                    panel.repaint();
                }
                loading = false;
            }
        });

        JLabel statusLabel = new JLabel("                                                                                                                                                 " +
                "X: 0   Y: 0");

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                panel.add(statusLabel, BorderLayout.SOUTH);
                statusLabel.setText(String.valueOf("                                                                                                                                        " +
                        "X:" + x + "   Y:" + y));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                panel.add(statusLabel, BorderLayout.SOUTH);
                statusLabel.setText(String.valueOf("                                                                                                                                        " +
                        "X:" + x + "   Y:" + y));
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Main());
    }

}
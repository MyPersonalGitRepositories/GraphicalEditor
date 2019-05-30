import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    // Режим рисования 
    private static int rezhim = 0;
    private static int xPad;
    private static int xf;
    private static int yf;
    private static int yPad;
    private static boolean pressed = false;
    private JSlider slider;
    private static final int thickness_MIN = 2;
    private static final int thickness_MAX = 30;
    private static final int thickness_INIT = 15;
    // текущий цвет
    private static Color maincolor;
    private static MyFrame frame;
    private static MyPanel canvas;
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
        maincolor = Color.black;

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
//        menuBar.setBounds(0, 0, 800, 30);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        Action exitAction = new AbstractAction("Вийти") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                System.exit(0);
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
                        loading = true;
                        frame.setSize(imag.getWidth() + 40, imag.getWidth() + 80);
                        canvas.setSize(imag.getWidth(), imag.getWidth());
                        canvas.repaint();
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

        canvas = new MyPanel();
        canvas.setBackground(Color.white);
        canvas.setOpaque(true);
        frame.add(canvas, BorderLayout.CENTER);

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

        JButton zalivkaButton = new JButton(new ImageIcon("zalivka.png"));
        zalivkaButton.setPreferredSize(Preferences.BUTTON_DIMENSION);
        zalivkaButton.addActionListener(event -> rezhim = 7);
        toolbar.add(zalivkaButton);

        frame.add(toolbar, BorderLayout.WEST);

        // Тулбар для кнопок
        JPanel colorbar = new JPanel();
        colorbutton = new JButton();
        colorbar.setPreferredSize(Preferences.TOOLBAR_DIMENSION);
        colorbutton.setBackground(maincolor);
        colorbutton.setPreferredSize(Preferences.MAIN_BUTTON_DIMENSION);
        Border etched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createLineBorder(Color.DARK_GRAY, 6);
        colorbutton.setBorder(titled);
        colorbutton.addActionListener(event -> {
            ColorDialog coldi = new ColorDialog(frame, "Вибір кольору");
            coldi.setVisible(true);
        });
        colorbar.add(colorbutton, BorderLayout.WEST);

        JButton redbutton = new JButton();
        redbutton.setBackground(Color.red);
        redbutton.setPreferredSize(Preferences.BUTTON_DIMENSION);
//        redbutton.setBounds(80, 5, 55, 15);
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

        canvas.addMouseMotionListener(new MouseMotionAdapter() {

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
                            //TODO ПОМЕНЯТЬ ШИРЕНУ И СОЗДАТЬ ДЛЯ ЭТОГО НОВЫЙ ФУНКЦИОНАЛ
//                            slider = new JSlider(JSlider.HORIZONTAL, thickness_MIN, thickness_MAX, thickness_INIT);
//                            slider.addChangeListener(e1 -> {});
//                            slider.setMajorTickSpacing(10);
//                            slider.setMinorTickSpacing(1);
//                            slider.setPaintTicks(true);
//                            slider.setPaintLabels(true);

                            g2.setStroke(new BasicStroke(3.0f));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // ластик
                        case 2:
                            //TODO
                            g2.setStroke(new BasicStroke(3.0f));
                            g2.setColor(Color.WHITE);
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // заливка
                        case 7:
                            g2.setStroke(new BasicStroke(10000.0f));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                    }
                    xPad = e.getX();
                    yPad = e.getY();
                }
                canvas.repaint();
            }
        });

        canvas.addMouseListener(new MouseAdapter() {

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
                        g2.setStroke(new BasicStroke(3.0f));
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // ластик
                    case 2:
                        g2.setStroke(new BasicStroke(3.0f));
                        g2.setColor(Color.WHITE);
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // текст
                    case 3:
                        // устанавливаем фокус для панели,
                        // чтобы печатать на ней текст
                        canvas.requestFocus();
                        break;
                    //заливка
                    case 7:
                        g2.setStroke(new BasicStroke(10000.0f));
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                }
                xPad = e.getX();
                yPad = e.getY();

                pressed = true;
                canvas.repaint();
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
                }
                xf = 0;
                yf = 0;
                pressed = false;
                canvas.repaint();
            }
        });
        canvas.addKeyListener(new KeyAdapter() {
            public void keyRelрeased(KeyEvent e) {
                // устанавливаем фокус для панели,
                // чтобы печатать на ней текст
                canvas.requestFocus();
            }

            public void keyTyped(KeyEvent e) {
                if (rezhim == 3) {
                    Graphics g = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(maincolor);
                    //TODO
                    g2.setStroke(new BasicStroke(2.0f));

                    String str = "";
                    str += e.getKeyChar();
                    //TODO
                    g2.setFont(new Font("Arial", 0, 26));
                    g2.drawString(str, xPad, yPad);
                    xPad += 10;
                    // устанавливаем фокус для панели,
                    // чтобы печатать на ней текст
                    canvas.requestFocus();
                    canvas.repaint();
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                // если делаем загрузку, то изменение размеров формы
                // отрабатываем в коде загрузки

                if (!loading) {
                    canvas.setSize(frame.getWidth() - 40, frame.getHeight() - 80);
                    BufferedImage tempImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D d2 = tempImage.createGraphics();
                    d2.setColor(Color.white);
                    d2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    tempImage.setData(imag.getRaster());
                    imag = tempImage;
                    canvas.repaint();
                }
                loading = false;
            }
        });


        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Main());
    }

}
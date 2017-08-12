package me.ulysse.iaspirateur.gui;

import me.ulysse.iaspirateur.ia.Robot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

// TODO rename GraphicalInterface because confusing
public class Interface implements ClassOpener {
    protected JFrame frame;
    private Menu menu;
    private final JFileChooser      _fileChooser = new JFileChooser(".");
    private Viewport                _viewport;
    private JLabel                  _statusBar;
    private ObjectEngine            _objectEngine;

    private final RobotEngine robotEngine;
    protected boolean over;

    public JFrame getFrame() { return frame; }
    public Menu getMenu() { return menu; }
    public JFileChooser getFileChooser() { return _fileChooser; }
    public Viewport getViewport() { return _viewport; }
    public JLabel getStatusBar() { return _statusBar; }
    public ObjectEngine getObjectEngine() { return _objectEngine; }

    // Ugly to pass robot here, but not the point here
    public Interface(Robot robot) {
        setCrossPlatformLookAndFeel();

        menu = new Menu(this);

        _viewport = new Viewport(this);
        _statusBar = new JLabel("Welcome !");
        frame = new JFrame("Module Machine Learning - ObjectEngine");

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }});

        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)  {
                ObjectEngine mli = getObjectEngine();
                if(mli != null)
                    mli.keyPressed(e.getKeyChar());

            }
            public void keyReleased(KeyEvent e)  {
                ObjectEngine mli = getObjectEngine();
                if(mli != null)
                    mli.keyReleased(e.getKeyChar());

            }
            public void keyTyped(KeyEvent e)  {
                ObjectEngine mli = getObjectEngine();
                if(mli != null)
                    mli.keyTyped(e.getKeyChar());

            }});

        // Screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - (screenSize.width / 2)) / 2,
                (screenSize.height - (screenSize.height / 2)) / 2,
                screenSize.width / 2,
                screenSize.height / 2);
//        frame.setBounds(100, 100, 800, 400);

        frame.setJMenuBar(menu);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(_viewport, BorderLayout.CENTER);
        frame.getContentPane().add(_statusBar, BorderLayout.SOUTH);
        frame.setVisible(true);

        classOpened(new ObjectEngine(), this);
        robotEngine = new RobotEngine(robot, this);
        classOpened(robotEngine, this);
//        menu.get_classLoadMenu().openClass("me.ulysse.iaspirateur.gui.RobotEngine");
    }

    private void setCrossPlatformLookAndFeel() {
        String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException |IllegalAccessException | UnsupportedLookAndFeelException  e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void classOpened(Object object, Object caller) {
    	if(over) {
    		return;
    	}
        if(_objectEngine != null)
            _objectEngine.terminate(); // supprimer les menus sp��cifiques
        _objectEngine = (ObjectEngine) object;
        _objectEngine.initInterface(this); // important !
        //_objectEngine.write();
        _viewport.draw();
        String nom = object.getClass().getName();
        frame.setTitle("Module Machine Learning - " + nom);
        _statusBar.setText("Class " + nom + " loaded");
    }

    public void redraw() {
        _viewport.draw();
    }

    public RobotEngine robotEngine() {
        return robotEngine;
    }

    //    public static void main(String[] args) {
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new Interface();
//                // ici, on pourrait mettre le .class �� charger: args[0]
//            }
//        });
//    }
}

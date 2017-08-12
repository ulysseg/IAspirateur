package me.ulysse.iaspirateur.gui;

import java.awt.event.*;
import javax.swing.*;

public class Menu extends JMenuBar implements ActionListener {
	private final Interface _interface;
	private JMenuItem _exitItem;
	private JMenuItem _aboutItem;
	private JMenu _fileMenu;
	private AboutBox _aboutBox;
	private ClassLoadMenu _classLoadMenu;

	public ClassLoadMenu get_classLoadMenu() {
		return _classLoadMenu;
	}

	public Menu(Interface _interface) {
		this._interface = _interface;

		_fileMenu = new JMenu("Start");
		_fileMenu.setMnemonic('S');

		_classLoadMenu = new ClassLoadMenu("Interface", _interface, ObjectEngine.class, _interface);
		_fileMenu.add(_classLoadMenu.getMenu());

		_fileMenu.addSeparator();

		_aboutItem = new JMenuItem("About");
		_aboutItem.addActionListener(this);
		_fileMenu.add(_aboutItem);

		_fileMenu.addSeparator();

		_exitItem = new JMenuItem("Exit");
		_exitItem.addActionListener(this);
		_fileMenu.add(_exitItem);
		add(_fileMenu);
	}

	public void addMenu(JMenu menu) {
		add(menu);
		validate();
		repaint();
	}

	public void removeMenu(JMenu menu) {
		remove(menu);
		validate();
		repaint();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == _aboutItem){
			if(_aboutBox == null)
				_aboutBox = new AboutBox();
			_aboutBox.getFrame().setVisible(true);
			return;
		}
		if (event.getSource() == _exitItem) {
			System.exit(0);
		}
	}
	
}

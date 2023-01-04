package pmr.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import pmr.PMR;

public class PanelTT extends JFrame implements KeyListener {
	public PMR pmr;
	public Display display;
	public PanelTT(PMR pmr)
	{
		this.pmr = pmr;
		display = new Display();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(display);
		add(new Luces(this));
		add(new Botones(this));
		getContentPane().setBackground(new Color(21,21,21));
		pack();
		setVisible(true);
		setTitle("Puesto movil Tren Tierra");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		setFocusable(true);
        setFocusTraversalKeysEnabled(false);
	}
	@Override
	public void keyPressed(KeyEvent key) {
		System.out.println();
		if (key.getKeyCode() == KeyEvent.VK_F && key.isControlDown()) setAlwaysOnTop(!isAlwaysOnTop()); 
	}
	@Override
	public void keyReleased(KeyEvent key) {
	}
	@Override
	public void keyTyped(KeyEvent key) {
	}
}

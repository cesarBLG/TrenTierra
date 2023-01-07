package pmr.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import pmr.PMR;

public class PanelTT extends JFrame implements KeyListener {
	public PMR pmr;
	public Display display;
	public static float Scale=0.5f;
	public PanelTT(PMR pmr)
	{
		this.pmr = pmr;
		display = new Display();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(display);
		Luces luces = new Luces(this);
		Botones botones = new Botones(this);
		add(luces);
		add(botones);
		getContentPane().setBackground(new Color(21,21,21));
		setTitle("Puesto movil Tren Tierra");
		pack();
		setSize(593, 337);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        this.addComponentListener(new ComponentListener() 
		{
			@Override
			public void componentHidden(ComponentEvent arg0) {}
			@Override
			public void componentMoved(ComponentEvent arg0) {}
			@Override
			public void componentResized(ComponentEvent arg0) {
				Dimension size = getSize();
				float scale = (float) Math.min(size.getHeight()/337, size.getWidth()/593);
				display.resize(scale);
				luces.resize(scale);
				botones.resize(scale);
			}
			@Override
			public void componentShown(ComponentEvent arg0) {}
		});
	}
	@Override
	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_F && key.isControlDown()) setAlwaysOnTop(!isAlwaysOnTop()); 
	}
	@Override
	public void keyReleased(KeyEvent key) {
	}
	@Override
	public void keyTyped(KeyEvent key) {
	}
}

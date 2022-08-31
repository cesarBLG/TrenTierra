package pmr.gui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import pmr.PMR;

public class PanelTT extends JFrame {
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
	}
}

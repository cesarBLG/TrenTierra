package pmr.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class Display extends JPanel {
	Digito digitos[] = new Digito[40];
	Display() {
		setBackground(Color.black);
		setOpaque(true);
		GridLayout g = new GridLayout(2,20);
		g.setHgap(10);
		g.setVgap(10);
		setLayout(g);
		for (int i=0; i<40; i++) {
			digitos[i] = new Digito();
			add(digitos[i]);
		}
	}
	public void setText(String text) {
		for (int i=0; i<40; i++) {
			if (i<text.length()) digitos[i].set(text.charAt(i));
			else digitos[i].set(' ');
		}
	}
}

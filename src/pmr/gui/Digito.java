package pmr.gui;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Digito extends JLabel {
	static HashMap<Character, ImageIcon> digitos = new HashMap<>();
	public Digito() {
		set(' ');
	}
	public void set(char txt) {
		ImageIcon icon;
		if (digitos.containsKey(txt))
		{
			icon = digitos.get(txt);
		}
		else
		{
			String nom = "";
			if (txt == ' ') nom = "espacio";
			else if (txt == '?') nom = "interrogacion";
			else if (txt == '"') nom = "comillas";
			else if (txt == ':') nom = "dospuntos";
			else if (txt == '>') nom = "mayor";
			else if (txt == '<') nom = "menor";
			else if (txt == '→') nom = "flechader";
			else nom += txt;
			URL location = getClass().getResource("/Content/Digitos/"+nom+".png");
	    	ImageIcon ic = new ImageIcon(location);
	    	Image img = ic.getImage();
	    	Image newimg = img.getScaledInstance(img.getWidth(ic.getImageObserver())/3, img.getHeight(ic.getImageObserver())/3, java.awt.Image.SCALE_SMOOTH);
			icon = new ImageIcon(newimg);
			digitos.put(txt, icon);
		}
		setIcon(icon);
	}
}

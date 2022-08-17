package pmr.gui;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import pmr.PMR;

public class LuzTT extends JLabel {
	static List<LuzTT> luces = new ArrayList<>();
	PMR pmr;
	LuzTT(TipoLuz tipo, PMR pmr)
	{
		this.pmr = pmr;
		this.tipo = tipo;
		luces.add(this);
		{
			URL location = getClass().getResource("/Content/Luces/"+tipo.name()+".png");
		    ImageIcon ic = new ImageIcon(location);
		    Image img = ic.getImage();
		    Image newimg = img.getScaledInstance(img.getWidth(ic.getImageObserver())/2, img.getHeight(ic.getImageObserver())/2, java.awt.Image.SCALE_SMOOTH);
			on = new ImageIcon(newimg);
		}
		{
			URL location = getClass().getResource("/Content/Luces/"+tipo.name()+"_off.png");
		    ImageIcon ic = new ImageIcon(location);
		    Image img = ic.getImage();
		    Image newimg = img.getScaledInstance(img.getWidth(ic.getImageObserver())/2, img.getHeight(ic.getImageObserver())/2, java.awt.Image.SCALE_SMOOTH);
			off = new ImageIcon(newimg);
		}
	}
	enum TipoLuz
	{
		RF,
		OC,
		INT,
		megafonia,
		TDC,
		ML,
		CON
	}
	TipoLuz tipo;
	ImageIcon on;
	ImageIcon off;
	boolean encendida()
	{
		if (pmr.client == null) return false;
		if (tipo == TipoLuz.RF && pmr.client.rf()) return true;
		else if (tipo == TipoLuz.OC && pmr.client.ocupado()) return true;
		else if (tipo == TipoLuz.CON && pmr.client.con()) return true;
		else if (tipo == TipoLuz.ML && pmr.descolgado) return true;
		return false;
	}
	static public void update()
	{
		for (LuzTT l : luces)
		{
			if (l.encendida()) l.setIcon(l.on);
			else l.setIcon(l.off);
		}
	}
}

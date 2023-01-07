package pmr.gui;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pmr.PMR;

public class LuzTT extends JLabel {
	static List<LuzTT> luces = new ArrayList<>();
	PMR pmr;
	String name_luz;
	LuzTT(TipoLuz tipo, PMR pmr)
	{
		this.pmr = pmr;
		this.tipo = tipo;
		//setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		luces.add(this);
		name_luz = tipo == TipoLuz.CON ? "CONL" : tipo.name();
		resize(1);
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
	public void update()
	{
		setIcon(encendida() ? on : off);
	}
	void resize(float scale)
	{
		scale /= 2;
		{
			URL location = getClass().getResource("/Content/Luces/"+name_luz+".png");
		    ImageIcon ic = new ImageIcon(location);
		    Image img = ic.getImage();
		    Image newimg = img.getScaledInstance((int)(img.getWidth(ic.getImageObserver())*scale), (int)(img.getHeight(ic.getImageObserver())*scale), java.awt.Image.SCALE_SMOOTH);
			on = new ImageIcon(newimg);
		}
		{
			URL location = getClass().getResource("/Content/Luces/"+name_luz+"_off.png");
		    ImageIcon ic = new ImageIcon(location);
		    Image img = ic.getImage();
		    Image newimg = img.getScaledInstance((int)(img.getWidth(ic.getImageObserver())*scale), (int)(img.getHeight(ic.getImageObserver())*scale), java.awt.Image.SCALE_SMOOTH);
			off = new ImageIcon(newimg);
		}
	}
}

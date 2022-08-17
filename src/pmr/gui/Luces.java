package pmr.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import pmr.gui.BotonTT.TipoBoton;
import pmr.gui.LuzTT.TipoLuz;

public class Luces extends JPanel {
	PanelTT panel;
	public Luces(PanelTT panel)
	{
		this.panel = panel;
		setOpaque(false);
		
		FlowLayout layout = new FlowLayout();
		layout.setHgap(40);
		setLayout(layout);
		
		add(new LuzTT(TipoLuz.RF, panel.pmr));
		add(new LuzTT(TipoLuz.OC, panel.pmr));
		add(new LuzTT(TipoLuz.INT, panel.pmr));
		add(new LuzTT(TipoLuz.megafonia, panel.pmr));
		add(new LuzTT(TipoLuz.TDC, panel.pmr));
		add(new LuzTT(TipoLuz.ML, panel.pmr));
		add(new LuzTT(TipoLuz.CON, panel.pmr));
		
		new Timer(250, (arg0) -> {LuzTT.update();}).start();
	}
}

package pmr.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import pmr.gui.BotonTT.TipoBoton;
import pmr.gui.LuzTT.TipoLuz;

public class Luces extends JPanel {
	PanelTT panel;
	List<LuzTT> luces = new ArrayList<>();
	public Luces(PanelTT panel)
	{
		this.panel = panel;
		setOpaque(false);
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.anchor = GridBagConstraints.CENTER;
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		luces.add(new LuzTT(TipoLuz.RF, panel.pmr));
		luces.add(new LuzTT(TipoLuz.OC, panel.pmr));
		luces.add(new LuzTT(TipoLuz.INT, panel.pmr));
		luces.add(new LuzTT(TipoLuz.megafonia, panel.pmr));
		luces.add(new LuzTT(TipoLuz.TDC, panel.pmr));
		luces.add(new LuzTT(TipoLuz.ML, panel.pmr));
		luces.add(new LuzTT(TipoLuz.CON, panel.pmr));
		
		for (LuzTT luz : luces)
		{
			add(luz, g);
			g.gridx++;
		}
		
		new Timer(250, (arg0) -> {
			for (LuzTT luz : luces)
			{
				luz.update();
			}
		}).start();
	}
	void resize(float scale)
	{
		for (LuzTT luz : luces)
		{
			luz.resize(scale);
		}
	}
}

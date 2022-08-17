package pmr.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import pmr.gui.BotonTT.TipoBoton;

public class PanelAlfanumerico extends JPanel {
	public PanelAlfanumerico()
	{
		setOpaque(false);
		
		BotonTT ml = new BotonTT(TipoBoton.ML);
		BotonTT n0 = new BotonTT(TipoBoton.N0);
		BotonTT onoff = new BotonTT(TipoBoton.OnOff);
		BotonTT n1 = new BotonTT(TipoBoton.N1);
		BotonTT n2 = new BotonTT(TipoBoton.N2);
		BotonTT n3 = new BotonTT(TipoBoton.N3);
		BotonTT n4 = new BotonTT(TipoBoton.N4);
		BotonTT n5 = new BotonTT(TipoBoton.N5);
		BotonTT n6 = new BotonTT(TipoBoton.N6);
		BotonTT n7 = new BotonTT(TipoBoton.N7);
		BotonTT n8 = new BotonTT(TipoBoton.N8);
		BotonTT n9 = new BotonTT(TipoBoton.N9);
		BotonTT modo = new BotonTT(TipoBoton.Modo);
		BotonTT ncanal = new BotonTT(TipoBoton.Canal);
		BotonTT ntren = new BotonTT(TipoBoton.Tren);
		
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.insets = new Insets(3, 3, 3, 3);
		
		add(ml, g);
		g.gridx++;
		add(n0, g);
		g.gridx++;
		add(onoff, g);
		g.gridx=0;
		g.gridy++;
		add(n1, g);
		g.gridx++;
		add(n2, g);
		g.gridx++;
		add(n3, g);
		g.gridx=0;
		g.gridy++;
		add(n4, g);
		g.gridx++;
		add(n5, g);
		g.gridx++;
		add(n6, g);
		g.gridx=0;
		g.gridy++;
		add(n7, g);
		g.gridx++;
		add(n8, g);
		g.gridx++;
		add(n9, g);
		g.gridx=0;
		g.gridy++;
		add(modo, g);
		g.gridx++;
		add(ncanal, g);
		g.gridx++;
		add(ntren, g);
	}
}

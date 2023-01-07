package pmr.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pmr.gui.BotonTT.TipoBoton;

public class PanelAlfanumerico extends JPanel {
	List<BotonTT> botones = new ArrayList<>();
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
		g.weighty = g.weightx = 1;
		g.fill = GridBagConstraints.BOTH;
		g.insets = new Insets(3, 3, 3, 3);
		
		botones.add(ml);
		botones.add(n0);
		botones.add(onoff);
		botones.add(n1);
		botones.add(n2);
		botones.add(n3);
		botones.add(n4);
		botones.add(n5);
		botones.add(n6);
		botones.add(n7);
		botones.add(n8);
		botones.add(n9);
		botones.add(modo);
		botones.add(ncanal);
		botones.add(ntren);
		
		int i=0;
		for (BotonTT boton : botones)
		{
			g.gridx = i%3;
			g.gridy = i/3;
			add(boton, g);
			i++;
		}
	}
	void resize(float scale)
	{
		for (BotonTT boton : botones)
		{
			boton.resize(scale);
		}
	}
}

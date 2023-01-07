package pmr.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import pmr.gui.BotonTT.TipoBoton;

public class Botones extends JPanel {
	List<BotonTT> botones = new ArrayList<>();
	PanelAlfanumerico numeros;
	public Botones(PanelTT panel) {

		BotonTT pidohablar = new BotonTT(TipoBoton.PidoHablar);
		BotonTT entrobanda = new BotonTT(TipoBoton.EntroBanda);
		BotonTT bien = new BotonTT(TipoBoton.Bien);
		BotonTT detenidosenal = new BotonTT(TipoBoton.DetenidoSeñal);
		BotonTT detenidotren = new BotonTT(TipoBoton.DetenidoTren);
		BotonTT megafonia = new BotonTT(TipoBoton.Megafonia);
		BotonTT continuomarcha = new BotonTT(TipoBoton.SigoMarcha);
		BotonTT averiaif = new BotonTT(TipoBoton.AveriaIF);
		BotonTT incidencia = new BotonTT(TipoBoton.Incidencia);
		BotonTT ext = new BotonTT(TipoBoton.ServicioExterior);
		BotonTT test = new BotonTT(TipoBoton.Test);
		BotonTT interfono = new BotonTT(TipoBoton.Interfono);
		BotonTT peticionvia = new BotonTT(TipoBoton.PeticionVia);
		BotonTT llegada = new BotonTT(TipoBoton.LlegadaPunto);
		BotonTT salida = new BotonTT(TipoBoton.SalidaPunto);
		BotonTT iniciombra = new BotonTT(TipoBoton.PeticionManiobra);
		BotonTT finmbra = new BotonTT(TipoBoton.FinManiobra);
		BotonTT vol = new BotonTT(TipoBoton.Volumen);
		BotonTT emergencia = new BotonTT(TipoBoton.Emergencia);
		BotonTT clr = new BotonTT(TipoBoton.CLR);
		BotonTT texto = new BotonTT(TipoBoton.TXT);
		BotonTT confirmacion = new BotonTT(TipoBoton.Confirmacion);
		
		setBackground(new Color(21,21,21));
		
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.anchor = GridBagConstraints.CENTER;
		g.weightx = g.weighty = 1;
		g.insets = new Insets(10, 3, 10, 3);
		g.gridx = g.gridy = 0;
		botones.add(pidohablar);
		botones.add(entrobanda);
		botones.add(bien);
		botones.add(detenidosenal);
		botones.add(detenidotren);
		botones.add(megafonia);
		botones.add(continuomarcha);
		botones.add(averiaif);
		botones.add(incidencia);
		botones.add(ext);
		botones.add(test);
		botones.add(interfono);
		botones.add(peticionvia);
		botones.add(llegada);
		botones.add(salida);
		botones.add(iniciombra);
		botones.add(finmbra);
		botones.add(vol);
		int i=0;
		for (BotonTT boton : botones)
		{
			g.gridx = i%6;
			g.gridy = i/6;
			add(boton, g);
			i++;
		}

		g.insets = new Insets(10, 15, 10, 15);
		g.gridx=6;
		g.gridy=1;
		botones.add(clr);
		add(clr, g);
		g.gridx++;
		botones.add(texto);
		add(texto, g);
		g.gridx=6;
		g.gridy=0;
		g.gridwidth = 2;
		botones.add(emergencia);
		add(emergencia, g);
		g.gridy=2;
		botones.add(confirmacion);
		add(confirmacion, g);
		
		g.gridy = 0;
		g.gridx = 8;
		g.gridheight = 3;
		g.fill = GridBagConstraints.BOTH;
		g.insets = new Insets(0, 3, 0, 3);
		numeros = new PanelAlfanumerico();
		add(numeros, g);
		
		BotonTT.setPressedActions(panel);
	}
	void resize(float scale)
	{
		for (BotonTT boton : botones)
		{
			boton.resize(scale);
		}
		numeros.resize(scale);
	}
}

package pmr.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import pmr.gui.BotonTT.TipoBoton;

public class Botones extends JPanel {
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
		
		setBackground(Color.black);
		
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.anchor = GridBagConstraints.CENTER;
		g.insets = new Insets(10, 3, 10, 3);
		g.gridx = g.gridy = 0;
		add(pidohablar, g);
		g.gridx++;
		add(entrobanda, g);
		g.gridx++;
		add(bien, g);
		g.gridx++;
		add(detenidosenal, g);
		g.gridx++;
		add(detenidotren, g);
		g.gridx++;
		add(megafonia, g);
		g.gridx=0;
		g.gridy++;
		add(continuomarcha, g);
		g.gridx++;
		add(averiaif, g);
		g.gridx++;
		add(incidencia, g);
		g.gridx++;
		add(ext, g);
		g.gridx++;
		add(test, g);
		g.gridx++;
		add(interfono, g);
		g.gridx = 0;
		g.gridy++;
		add(peticionvia, g);
		g.gridx++;
		add(llegada, g);
		g.gridx++;
		add(salida, g);
		g.gridx++;
		add(iniciombra, g);
		g.gridx++;
		add(finmbra, g);
		g.gridx++;
		add(vol, g);

		g.insets = new Insets(10, 15, 10, 15);
		g.gridx=6;
		g.gridy=1;
		add(clr, g);
		g.gridx++;
		add(texto, g);
		g.gridx=6;
		g.gridy=0;
		g.gridwidth = 2;
		add(emergencia, g);
		g.gridy=2;
		add(confirmacion, g);
		
		g.gridy = 0;
		g.gridx = 8;
		g.gridheight = 3;
		g.insets = new Insets(0, 3, 0, 3);
		add(new PanelAlfanumerico(), g);
		
		BotonTT.setPressedActions(panel);
	}
}

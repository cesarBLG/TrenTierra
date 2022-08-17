package pcr.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import pmr.PMR.Mensajes;

public class BotonPCR extends JButton {
	static List<BotonPCR> botones = new ArrayList<>();
	enum Botones
	{
		Hable,
		SupresionParada,
		ConexionMegafonia,
		ReduzcaMarcha,
		AutModC,
		AtentoSeñal,
		BajePantografo,
		SubaPantografo,
		AltoUrgente,
		AtPasoNivel,
		ConcesionVia,
		ConcesionManiobra,
		ConformeTierra,
		HableConPMovil,
		Texto,
		LlamadaGeneral;
	}
	Botones tipo;
	BotonPCR(String text, Botones tipo)
	{
		super(text);
		this.tipo = tipo;
		botones.add(this);
	}
	public static void setPressedActions(PanelPCR panel)
	{
		for (BotonPCR b : botones)
		{
			b.addActionListener((arg0) -> {
				if (b.tipo == Botones.Texto) panel.sendText();
				else panel.send(Mensajes.valueOf(b.tipo.name()));
			});
		}
	}
}

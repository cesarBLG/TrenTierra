package pcr.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import pcr.PCR;
import pcr.gui.BotonPCR.Botones;
import pmr.PMR.Mensajes;


public class PanelPCR extends JFrame {

	public PCR pcr;
	public JLabel display;
	JTextField ntren;
	public PanelPCR(PCR pcr)
	{
		this.pcr = pcr;
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		display = new JLabel(/*"Sin mensajes codificados"*/);
		ntren = new JTextField("Numero de tren");
		JButton listatrenes = new JButton("Trenes en línea");
		listatrenes.addActionListener((arg0) -> {
			List<Integer> old = new ArrayList<>();
			for (Integer i : pcr.listatrenes.keySet())
			{
				if (pcr.listatrenes.get(i)<System.currentTimeMillis()-30000) old.add(i);
			}
			for (Integer i : old)
			{
				pcr.listatrenes.remove(i);
			}
			String trenes = "";
			for (Integer i : pcr.listatrenes.keySet())
			{
				trenes += i.toString() + "\n";
			}
			JOptionPane.showMessageDialog(null, trenes, "Lista de trenes", JOptionPane.INFORMATION_MESSAGE);
		});
		add(listatrenes);
		add(display);
		add(ntren);
		add(new BotonPCR("Hable", Botones.Hable));
		add(new BotonPCR("Conexión Megafonia", Botones.ConexionMegafonia));
		add(new BotonPCR("Autorizo sistema C", Botones.AutModC));
		add(new BotonPCR("Suprima parada", Botones.SupresionParada));
		add(new BotonPCR("Atento a señal", Botones.AtentoSeñal));
		add(new BotonPCR("Reduzca marcha", Botones.ReduzcaMarcha));
		add(new BotonPCR("Baje pantografo", Botones.BajePantografo));
		add(new BotonPCR("Suba pantografo", Botones.SubaPantografo));
		add(new BotonPCR("Parada inmediata", Botones.AltoUrgente));
		add(new BotonPCR("Texto", Botones.Texto));
		add(new BotonPCR("Llamada general", Botones.LlamadaGeneral));
		BotonPCR.setPressedActions(this);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	void send(Mensajes msg)
	{
		pcr.enviarMensaje(msg, Integer.parseInt(ntren.getText()));
	}
	void sendText()
	{
		pcr.enviarTexto(JOptionPane.showInputDialog("Texto a enviar"), Integer.parseInt(ntren.getText()));
	}
}

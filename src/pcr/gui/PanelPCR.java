package pcr.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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


public class PanelPCR extends JFrame implements KeyListener {

	public PCR pcr;
	public JLabel display;
	JTextField ntren;
	public List<BotonPCR> botones = new ArrayList<>();
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
		botones.add(new BotonPCR("Hable", Botones.Hable));
		botones.add(new BotonPCR("Conexión Megafonia", Botones.ConexionMegafonia));
		botones.add(new BotonPCR("Conforme", Botones.ConformeTierra));
		botones.add(new BotonPCR("Autorizo sistema C", Botones.AutModC));
		botones.add(new BotonPCR("Suprima parada", Botones.SupresionParada));
		botones.add(new BotonPCR("Atento a señal", Botones.AtentoSeñal));
		botones.add(new BotonPCR("Reduzca marcha", Botones.ReduzcaMarcha));
		botones.add(new BotonPCR("Baje pantografo", Botones.BajePantografo));
		botones.add(new BotonPCR("Suba pantografo", Botones.SubaPantografo));
		botones.add(new BotonPCR("Parada inmediata", Botones.AltoUrgente));
		botones.add(new BotonPCR("Texto", Botones.Texto));
		botones.add(new BotonPCR("Llamada general", Botones.LlamadaGeneral));
		for (BotonPCR b : botones)
		{
			add(b);
		}
		BotonPCR.setPressedActions(this);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		setFocusable(true);
	    setFocusTraversalKeysEnabled(false);
	}
	void send(Mensajes msg)
	{
		int trn = -1;
		try {
			trn = Integer.parseInt(ntren.getText());
		} catch (Exception e) {
			
		}
		pcr.enviarMensaje(msg, trn);
	}
	void sendText()
	{
		pcr.enviarTexto(JOptionPane.showInputDialog("Texto a enviar"), Integer.parseInt(ntren.getText()));
	}
	@Override
	public void keyPressed(KeyEvent key) {
		System.out.println();
		if (key.getKeyCode() == KeyEvent.VK_F && key.isControlDown()) setAlwaysOnTop(!isAlwaysOnTop()); 
	}
	@Override
	public void keyReleased(KeyEvent key) {
	}
	@Override
	public void keyTyped(KeyEvent key) {
	}
}

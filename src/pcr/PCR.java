package pcr;

import java.util.Dictionary;
import java.util.HashMap;

import javax.swing.JOptionPane;

import pcr.gui.BotonPCR;
import pcr.gui.PanelPCR;
import pmr.PMR.Mensajes;
import pmr.gui.BotonTT;

public class PCR {
	Server server;
	PanelPCR panel;
	int canal;
	public HashMap<Integer, Long> listatrenes = new HashMap<>();
	boolean hablando;
	boolean llamadaGeneral = false;
	int hable_pm1=-1;
	int hable_pm2=-1;
	public static void main(String args[])
	{
		new PCR();
	}
	HashMap<Mensajes, String> mensajes_tren = new HashMap<>();
	PCR()
	{
		canal = Integer.parseInt(JOptionPane.showInputDialog("Numero de canal"));
		server = new Server(this);
		panel = new PanelPCR(this);
		panel.setTitle("Puesto central. Canal "+canal);
		mensajes_tren.put(Mensajes.PidoHablar, "PIDO HABLAR");
		mensajes_tren.put(Mensajes.EntroBanda, "ENTRO BANDA");
		mensajes_tren.put(Mensajes.ConformeTren, "CONFORME");
		mensajes_tren.put(Mensajes.DetenidoSeñal, "DETENIDO ANTE SEÑAL");
		mensajes_tren.put(Mensajes.DetenidoTren, "DETENIDO TREN");
		mensajes_tren.put(Mensajes.SigoMarcha, "SIGO MARCHA");
		mensajes_tren.put(Mensajes.AveriaIF, "AVERIA IF");
		mensajes_tren.put(Mensajes.Incidencia, "INCIDENCIA");
		mensajes_tren.put(Mensajes.ServicioExterior, "SERVICIO EXTERIOR");
		mensajes_tren.put(Mensajes.PeticionVia, "PETICION VIA");
		mensajes_tren.put(Mensajes.LlegadaPunto, "LLEGADA A UN PUNTO");
		mensajes_tren.put(Mensajes.SalidaPunto, "SALIDA DE UN PUNTO");
		mensajes_tren.put(Mensajes.PeticionManiobra, "PETICION MANIOBRA");
		mensajes_tren.put(Mensajes.FinManiobra, "FIN DE MANIOBRA");
		mensajes_tren.put(Mensajes.Emergencia, "EMERGENCIA");
	}
	public void enviarMensaje(Mensajes mensaje, int tren)
	{
		if (mensaje == Mensajes.LlamadaGeneral)
		{
			if (llamadaGeneral)
			{
				llamadaGeneral = hablando = false;
				for (BotonPCR b : panel.botones)
				{
					b.setEnabled(true);
				}
			}
			else
			{
				for (BotonPCR b : panel.botones)
				{
					b.setEnabled("Llamada general".equals(b.getText()));
				}
				llamadaGeneral = true;
				hablando = true;
				server.enviarMensaje(mensaje, 0xffffff);
				new Thread(() -> {
					server.enviarAudio(0xffffff);
				}).start();
			}
		}
		else
		{
			if (mensaje == Mensajes.Hable)
			{
				if (hablando)
				{
					for (BotonPCR b : panel.botones)
					{
						b.setEnabled(true);
					}
					hablando = false;
					return;
				}
				else
				{
					for (BotonPCR b : panel.botones)
					{
						b.setEnabled("Hable".equals(b.getText()));
					}
					hablando = true;
					new Thread(() -> {
						server.enviarAudio(tren);
					}).start();
				}
			}
			server.enviarMensaje(mensaje, tren);
		}
	}
	public synchronized void mensajeRecibido(Mensajes mensaje, int tren)
	{
		JOptionPane.showMessageDialog(null, "Canal "+canal+" Tren "+tren+": "+mensajes_tren.get(mensaje));
		if (mensaje == Mensajes.Emergencia)
		{
			llamadaGeneral = hablando = false;
			while(server.enviando)
			{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			enviarMensaje(Mensajes.Hable, tren);
		}
		//panel.display.setText();
	}
	public void enviarTexto(String texto, int tren)
	{
		server.enviarTexto(texto, tren);
	}
	public void textoRecibido(String texto, int tren)
	{
		JOptionPane.showMessageDialog(null, "Canal "+canal+" Tren "+tren+": "+texto);
		//panel.display.setText();
	}
}

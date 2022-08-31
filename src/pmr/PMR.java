package pmr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import javax.swing.Timer;

import pmr.gui.BotonTT;
import pmr.gui.BotonTT.TipoBoton;
import pmr.gui.PanelTT;
import streaming.Input;
import streaming.Output;

public class PMR {
	PanelTT panel;
	enum Modo
	{
		A,
		B,
		C,
		D
	}
	Modo modo = Modo.A;
	int canal = -1;
	int ntren = -1;
	
	public Client client;
	
	public static void main(String[] args)
	{
		new PMR();
	}
	
	public enum Mensajes
	{
		PidoHablar,
		EntroBanda,
		ConformeTren,
		DetenidoSeñal,
		DetenidoTren,
		SigoMarcha,
		AveriaIF,
		Incidencia,
		ServicioExterior,
		PeticionVia,
		LlegadaPunto,
		SalidaPunto,
		PeticionManiobra,
		FinManiobra,
		Emergencia,
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
		LlamadaGeneral;
		public int getId()
		{
			return ordinal();
		}
		public static Mensajes fromId(int Id)
		{
			return values()[Id];
		}
	}
	
	HashMap<Mensajes, String> mensajes_tierra = new HashMap<>();
	HashMap<Mensajes, String> mensajes_tren = new HashMap<>();

	enum Estado
	{
		Normal,
		IntroduceModo,
		IntroduceCanal,
		IntroduceTren,
		IntroduceHora,
		IntroduceTexto,
		TextoEnviado,
		MensajeSeleccionado,
		MensajeEnviado,
		MensajeRecibido,
		TextoRecibido,
		ComunicacionEstablecida,
		ComunicacionRecibida,
	}
	Estado estado;
	String datos_introduciendose;
	
	Mensajes mensaje_enviado;
	Mensajes mensaje_recibido;
	
	String texto_recibido;
	String texto_enviado;
	
	public boolean descolgado=false;
	
	class TextKeyboard
	{
		String cadena = "";
		int ultimo = -1;
		int index = -1;
		long tiempo;
		String[] nums = {"ABC0","DEF1","GHI2","JKL3","MNÑ4","OPQ5","RST6","UVW7","XYZ8"," -?9"};
		public boolean tiempo_pasado()
		{
			return tiempo + 2000 < System.currentTimeMillis();
		}
		String get(int pulsado)
		{
			if (tiempo_pasado() || pulsado != ultimo)
			{
				cadena += nums[pulsado].charAt(0);
				index = 0;
			}
			else
			{
				index = (index+1)%4;
				cadena = cadena.substring(0, cadena.length()-1) + nums[pulsado].charAt(index);
			}
			tiempo = System.currentTimeMillis();
			ultimo = pulsado;
			return cadena;
		}
	}
	TextKeyboard teclado_texto;
	
	boolean enviar_bien = false;
	
	boolean preguntar_hora = false;
	
	public PMR()
	{
		try
		{
			FileReader fileReader = new FileReader("pmr_cfg.ini");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null)
			{
				String[] token = line.trim().split("=");
				if (token.length == 2)
				{
					token[0] = token[0].trim();
					token[1] = token[1].trim();
	    			if (token[0].equalsIgnoreCase("preguntarhora"))
	    			{
	    				String val = token[1];
	    				preguntar_hora = val.equals("1") || val.equalsIgnoreCase("true");
	    			}
				}
	    		line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		estado = Estado.Normal;
		panel = new PanelTT(this);
		mensajes_tren.put(Mensajes.PidoHablar, "PIDO HABLAR");
		mensajes_tren.put(Mensajes.EntroBanda, "ENTRO BANDA");
		mensajes_tren.put(Mensajes.ConformeTren, "  C O N F O R M E");
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
		mensajes_tren.put(Mensajes.Hable, "    H A B L E");
		mensajes_tren.put(Mensajes.SupresionParada, " SUPRESION PARADA");
		mensajes_tren.put(Mensajes.ConexionMegafonia, "CONEXION MEGAFONIA");
		mensajes_tren.put(Mensajes.ReduzcaMarcha, "  REDUZCA MARCHA");
		mensajes_tren.put(Mensajes.AutModC, "AUT. MOD. C");
		mensajes_tren.put(Mensajes.AtentoSeñal, "  ATENTO SEÑAL");
		mensajes_tren.put(Mensajes.BajePantografo, " BAJE  PANTOGRAFO");
		mensajes_tren.put(Mensajes.SubaPantografo, " SUBA  PANTOGRAFO");
		mensajes_tren.put(Mensajes.AltoUrgente, "   ALTO URGENTE");
		mensajes_tren.put(Mensajes.AtPasoNivel, "AT. PASO A NIVEL");
		mensajes_tren.put(Mensajes.ConcesionVia, "CONCESION VIA");
		mensajes_tren.put(Mensajes.ConcesionManiobra, "CONCESION MANIOBRA");
		mensajes_tren.put(Mensajes.ConformeTierra, "  C O N F O R M E");
		mensajes_tren.put(Mensajes.HableConPMovil, "HABLE CON P. MOVIL");
		mensajes_tren.put(Mensajes.LlamadaGeneral, "LLAMADA GENERAL");
		updateState();
		new Timer(500, (arg0) -> {updateState();}).start();
		client = new Client(this);
	}
	String numero_canal()
	{
		if (canal < 0) return "__";
		return String.format("%02d", canal);
	}
	String numero_tren()
	{
		if (ntren < 0) return "______";
		return String.format("%06d", ntren);
	}
	void mensajeRecibido(Mensajes mensaje)
	{
		BotonTT.playSound("TT_Aviso.wav");
		mensaje_recibido = mensaje;
		if (mensaje_recibido == Mensajes.Hable || mensaje_recibido == Mensajes.HableConPMovil)
		{
			estado = Estado.ComunicacionEstablecida;
		}
		else if (mensaje_recibido == Mensajes.LlamadaGeneral || mensaje_recibido == Mensajes.ConexionMegafonia)
		{
			estado = Estado.ComunicacionRecibida;
		}
		else
		{
			estado = Estado.MensajeRecibido;
			enviar_bien = true;
		}
		updateState();
	}
	void textoRecibido(String texto)
	{
		BotonTT.playSound("TT_Aviso.wav");
		estado = Estado.TextoRecibido;
		texto_recibido = texto;
		updateState();
	}
	Timer envio_mensaje;
	void enviarMensaje(Mensajes mensaje)
	{
		if (envio_mensaje != null) envio_mensaje.stop();
		envio_mensaje = new Timer(1000, null);
		envio_mensaje.setRepeats(false);
		envio_mensaje.addActionListener((arg0) -> {
			if (mensaje_enviado == null || estado != Estado.MensajeEnviado)
			{
				if (envio_mensaje != null) envio_mensaje.stop();
				return;
			}
			new Thread(() -> {
			if (envio_mensaje != null) envio_mensaje.stop();
			boolean enviado = client.enviarMensaje(mensaje);
			if (enviado)
			{
				estado = Estado.Normal;
				mensaje_enviado = null;
				updateState();
			}
			else
			{
				if (envio_mensaje != null) envio_mensaje.start();
			}}).start();
		});
		envio_mensaje.start();
	}
	Timer envio_texto;
	void enviarTexto(String texto)
	{
		if (envio_texto != null) envio_texto.stop();
		envio_texto = new Timer(1000, null);
		envio_texto.setRepeats(false);
		envio_texto.addActionListener((arg0) -> {
			if (texto_enviado == null || estado != Estado.TextoEnviado)
			{
				if (envio_texto != null) envio_texto.stop();
				return;
			}
			new Thread(() -> {
			if (envio_texto != null) envio_texto.stop();
			boolean enviado = client.enviarTexto(texto);
			if (enviado)
			{
				estado = Estado.Normal;
				texto_enviado = null;
				updateState();
			}
			else
			{
				if (envio_texto != null) envio_texto.start();
			}}).start();
		});
		envio_texto.start();
	}
	void updateState()
	{
		String texto = "";
		if (mensaje_recibido!=null)
		{
			texto += "<"+mensajes_tren.get(mensaje_recibido);
			for (int i=mensajes_tren.get(mensaje_recibido).length(); i<18; i++)
			{
				texto += " ";
			}
			texto += ">";
		}
		else if (estado == Estado.TextoRecibido)
		{
			texto = "\""+texto_recibido.toUpperCase();
			for (int i=texto_recibido.length(); i<38; i++)
			{
				texto += " ";
			}
			texto += "\"";
		}
		else if (estado == Estado.TextoEnviado || estado == Estado.IntroduceTexto)
		{
			boolean t = teclado_texto.tiempo_pasado();
			texto = "\""+texto_enviado.toUpperCase()+(t?"_":"");
			for (int i=texto_enviado.length()+(t?1:0); i<38; i++)
			{
				texto += " ";
			}
			texto += "\"";
		}
		else
		{
			String datos = "MD:"+modo.name()+" CN:"+numero_canal();
			if (modo == Modo.A) datos += " TR:"+numero_tren();
			else datos += "          ";
			texto = datos;
		}
		if (estado == Estado.MensajeSeleccionado || estado == Estado.MensajeEnviado)
		{
			texto += mensajes_tren.get(mensaje_enviado);
			for (int i=mensajes_tren.get(mensaje_enviado).length(); i<19; i++)
			{
				texto += " ";
			}
			texto += estado == Estado.MensajeSeleccionado ? "→" : ">";
		}
		else if (estado == Estado.IntroduceCanal)
		{
			texto += ">NUMERO DE CANAL? " + datos_introduciendose;
			for (int i=0; i<2-datos_introduciendose.length(); i++)
			{
				texto += "_";
			}
		}
		else if (estado == Estado.IntroduceTren)
		{
			texto += ">NUMERO TREN? " + datos_introduciendose;
			for (int i=0; i<6-datos_introduciendose.length(); i++)
			{
				texto += "_";
			}
		}
		else if (estado == Estado.IntroduceModo)
		{
			texto += ">MODO.........?  " + datos_introduciendose;
		}
		else if (estado == Estado.IntroduceHora)
		{
			texto += ">HORA.............?";
		}
		else if (enviar_bien) texto += " ENVIAR \" B I E N \" ";
		
		panel.display.setText(texto);
	}
	public Mensajes getMensaje(TipoBoton code)
	{
		try
		{
			return Mensajes.valueOf(code.name());
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public void pulsacion(TipoBoton code)
	{
		boolean valida = false;
		int num=-1;
		if (code == TipoBoton.N0) num = 0;
		else if (code == TipoBoton.N1) num = 1;
		else if (code == TipoBoton.N2) num = 2;
		else if (code == TipoBoton.N3) num = 3;
		else if (code == TipoBoton.N4) num = 4;
		else if (code == TipoBoton.N5) num = 5;
		else if (code == TipoBoton.N6) num = 6;
		else if (code == TipoBoton.N7) num = 7;
		else if (code == TipoBoton.N8) num = 8;
		else if (code == TipoBoton.N9) num = 9;
		if (num >= 0)
		{
			if (estado == Estado.IntroduceCanal)
			{
				if (datos_introduciendose.length() < 2)
				{
					datos_introduciendose += Integer.toString(num);
					valida = true;
				}
			}
			else if (estado == Estado.IntroduceTren)
			{
				if (datos_introduciendose.length() < 6)
				{
					datos_introduciendose += Integer.toString(num);
					valida = true;
				}
			}
			else if (estado == Estado.IntroduceTexto)
			{
				texto_enviado = teclado_texto.get(num);
			}
		}
		else if (code == TipoBoton.Modo)
		{
			if (estado == Estado.IntroduceModo)
			{
				if (datos_introduciendose.equals("A")) datos_introduciendose = "C";
				else if (datos_introduciendose.equals("C")) datos_introduciendose = "D";
				else datos_introduciendose = "A";
				valida = true;
			}
			else if (estado == Estado.Normal)
			{
				estado = Estado.IntroduceModo;
				datos_introduciendose = modo.name();
				valida = true;
			}
		}
		else if (code == TipoBoton.Canal)
		{
			if (estado == Estado.Normal)
			{
				estado = Estado.IntroduceCanal;
				datos_introduciendose = "";
				canal = -1;
				valida = true;
			}
		}
		else if (code == TipoBoton.Tren)
		{
			if (estado == Estado.Normal && modo == Modo.A)
			{
				estado = Estado.IntroduceTren;
				datos_introduciendose = "";
				ntren = -1;
				valida = true;
			}
		}
		else if (code == TipoBoton.CLR)
		{
			if (estado == Estado.IntroduceCanal || estado == Estado.IntroduceTren)
			{
				datos_introduciendose = "";
				valida = true;
			}
			else if (estado == Estado.MensajeSeleccionado || estado == Estado.MensajeEnviado)
			{
				mensaje_enviado = null;
				estado = Estado.Normal;
				valida = true;
			}
			else if (estado == Estado.IntroduceTexto)
			{
				texto_enviado = null;
				estado = Estado.Normal;
				valida = true;
			}
			else if (estado == Estado.TextoRecibido)
			{
				texto_recibido = null;
				estado = Estado.Normal;
				valida = true;
			}
		}
		else if (code == TipoBoton.TXT)
		{
			if (estado == Estado.Normal)
			{
				estado = Estado.IntroduceTexto;
				texto_enviado = "";
				teclado_texto = new TextKeyboard();
				valida = true;
			}
		}
		else if (code == TipoBoton.Confirmacion)
		{
			if (estado == Estado.IntroduceCanal)
			{
				if (datos_introduciendose.length() == 2)
				{
					canal = Integer.parseInt(datos_introduciendose);
					estado = preguntar_hora ? Estado.IntroduceHora : Estado.Normal;
					valida = true;
				}
			}
			else if (estado == Estado.IntroduceTren)
			{
				if (datos_introduciendose.length() == 6)
				{
					ntren = Integer.parseInt(datos_introduciendose);
					estado = preguntar_hora ? Estado.IntroduceHora : Estado.Normal;
					valida = true;
				}
			}
			else if (estado == Estado.IntroduceModo)
			{
				if (datos_introduciendose.equals("C")) modo = Modo.C;
				else if (datos_introduciendose.equals("D")) modo = Modo.D;
				else  modo = Modo.A;
				canal = ntren = -1;
				estado = Estado.Normal;
				valida = true;
			}
			else if (estado == Estado.IntroduceHora)
			{
				estado = Estado.Normal;
				valida = true;
			}
			else if (estado == Estado.MensajeSeleccionado)
			{
				estado = Estado.MensajeEnviado;
				if (mensaje_enviado == Mensajes.ConformeTren)
				{
					enviar_bien = false;
					mensaje_recibido = null;
				}
				valida = true;
				enviarMensaje(mensaje_enviado);
			}
			else if (estado == Estado.IntroduceTexto)
			{
				estado = Estado.TextoEnviado;
				valida = true;
				enviarTexto(texto_enviado);
			}
		}
		else if (code == TipoBoton.ML)
		{
			if (estado == Estado.ComunicacionEstablecida)
			{
				if(descolgado)
				{
					estado = Estado.Normal;
					mensaje_recibido = null;
					descolgado = false;
				}
				else
				{
					descolgado = true;
					new Thread(() -> {client.enviarAudio();}).start();
				}
				valida = true;
			}
		}
		else if (code == TipoBoton.Emergencia)
		{
			estado = Estado.MensajeSeleccionado;
			mensaje_enviado = Mensajes.Emergencia;
			valida = true;
		}
		else if (code == TipoBoton.Bien)
		{
			if (enviar_bien)
			{
				estado = Estado.MensajeSeleccionado;
				mensaje_enviado = Mensajes.ConformeTren;
				valida = true;
			}
		}
		else if (getMensaje(code) != null)
		{
			if (modo == Modo.A && ntren != -1 && canal != -1 && estado != Estado.ComunicacionEstablecida && estado != Estado.ComunicacionRecibida)
			{
				estado = Estado.MensajeSeleccionado;
				mensaje_enviado = getMensaje(code);
				valida = true;
			}
		}
		else if (code == TipoBoton.OnOff)
		{
			estado = Estado.Normal;
			mensaje_enviado = mensaje_recibido = null;
			enviar_bien = false;
			canal = ntren = -1;
			valida = true;
		}
		if (valida) BotonTT.playSound("TT_Bip.wav");
		updateState();
	}
}

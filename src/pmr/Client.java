package pmr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import pmr.PMR.Estado;
import pmr.PMR.Mensajes;
import pmr.PMR.Modo;
import streaming.Input;
import streaming.Output;

public class Client {
	Socket client;
	int canalconectado = -1;
	PMR pmr;
	InputStream in;
	OutputStream out;
	Output audioOut;
	Hashtable<Integer, String> ips = new Hashtable<>();
	boolean p2p = false;
	boolean enviando;
	boolean recibiendo;
	Client(PMR pmr)
	{
		this.pmr = pmr;
		try
		{
			FileReader fileReader = new FileReader("pmr_ips.ini");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null)
			{
				String[] token = line.trim().split("=");
				if (token.length == 2)
				{
					String ip = token[0].trim();
    				String[] canales = token[1].trim().split(",");
    				for (String canal : canales)
    				{
    					ips.put(Integer.parseInt(canal), ip);
    				}
				}
	    		line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ips.isEmpty())
		{
			String ip = JOptionPane.showInputDialog("IP a la que conectarse");
			p2p = true;
			for (int i=0; i<100; i++)
			{
				ips.put(i, ip);
			}
		}
		new Timer(500, (arg0) -> {new Thread(() -> {update();}).start();}).start();
		new Timer(10000, (arg0) -> {new Thread(() -> {alive();}).start();}).start();
		new Thread(() -> {while(true) recibirMensaje();}).start();
	}
	synchronized void update()
	{
		if (pmr.canal == -1 || pmr.modo == Modo.C)
		{
			canalconectado = -1;
		}
		else if (pmr.canal != canalconectado)
		{
			try {
				if (client != null) client.close();
				client = new Socket(ips.getOrDefault(pmr.canal, "127.0.0.1"), p2p ? (48300 + pmr.canal) : 48301);
				in = client.getInputStream();
				out = client.getOutputStream();
				out.write(new byte[] {(byte)pmr.canal,(byte)1});
				canalconectado = pmr.canal;
				alive();
			} catch (IOException e) {
				client = null;
				canalconectado = -1;
				e.printStackTrace();
			}
		}
	}
	public boolean conectado()
	{
		return client != null && client.isConnected();
	}
	public boolean rf()
	{
		 return pmr.canal>=0 && !conectado();
	}
	public boolean ocupado()
	{
		return conectado() && recibiendo;
	}
	public boolean con()
	{
		if (conectado() && enviando) return true;
		if (pmr.estado == Estado.MensajeEnviado) return (System.currentTimeMillis()/500)%2==0;
		return false;
	}
	public synchronized boolean enviar(byte[] datos)
	{
		if (enviando) return false;
		if (pmr.ntren < 0) return false;
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		enviando = true;
		o.write((byte)(pmr.ntren/65536));
		o.write((byte)((pmr.ntren/256)%256));
		o.write((byte)(pmr.ntren%256));
		o.write(datos, 0, datos.length);
		byte[] b = o.toByteArray();
		try {
			for (int i=0; i<b.length; i++)
			{
				out.write(b[i]);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			enviando = false;
			return true;
		} catch (IOException e) {
			client = null;
			canalconectado = -1;
			e.printStackTrace();
		}
		enviando = false;
		return false;
	}
	public boolean enviarMensaje(Mensajes mensaje)
	{
		return enviar(new byte[]{(byte)mensaje.getId()});
	}
	public boolean enviarTexto(String texto)
	{
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write((byte)255);
		byte[] tb = texto.getBytes();
		o.write((byte)tb.length);
		o.write(tb, 0, tb.length);
		return enviar(o.toByteArray());
	}
	public void enviarAudio()
	{
		while(enviando) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		enviando = true;
		Input in=new Input();
		try {
			while(pmr.estado == Estado.ComunicacionEstablecida && pmr.descolgado)
			{
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				o.write((byte)(pmr.ntren/65536));
				o.write((byte)((pmr.ntren/256)%256));
				o.write((byte)(pmr.ntren%256));
				o.write((byte)253);
				o.write(in.getAudio());
				out.write(o.toByteArray());
			}
			out.write(new byte[] {(byte)255,(byte)255,(byte)255,(byte)252});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		enviando=false;
		in.stop();
	}
	public void alive()
	{
		if (client == null) return;
		enviar(new byte[]{(byte)254});
	}
	void recibirMensaje()
	{
		if (client == null)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try {
			int trn = in.read();
			if (trn == -1) throw new EOFException();
			recibiendo = true;
			trn = in.read() + trn * 256;
			trn = in.read() + trn * 256;
			int code = in.read();
			if (code != 253 && code != Mensajes.Hable.getId() && code != Mensajes.HableConPMovil.getId() && code != Mensajes.LlamadaGeneral.getId() && code != Mensajes.ConexionMegafonia.getId())
			{
				if (audioOut != null)
				{
					audioOut.stop();
					audioOut = null;
				}
				if (pmr.estado == Estado.ComunicacionEstablecida || pmr.estado == Estado.ComunicacionRecibida)
				{
					pmr.mensaje_recibido = null;
					pmr.estado = Estado.Normal;
					pmr.descolgado = false;
				}
			}
			if (code == 255)
			{
				int length = in.read();
				byte[] data = new byte[length];
				for (int i=0; i<length; i++)
				{
					data[i] = (byte)in.read();
				}
				if (trn == pmr.ntren) pmr.textoRecibido(new String(data));
			}
			else if (code == 253)
			{
				if (audioOut == null && trn == pmr.ntren) audioOut = new Output();
				byte[] data = new byte[Input.BUFFERSIZE];
				int numread = 0;
				while(numread<Input.BUFFERSIZE)
					numread += in.read(data, numread, Input.BUFFERSIZE-numread);
				if ((pmr.estado == Estado.ComunicacionEstablecida && pmr.descolgado) || pmr.estado == Estado.ComunicacionRecibida)
				{
					if (audioOut == null) audioOut = new Output();
					audioOut.play(data);
				}
			}
			else if (code == 252) {}
			else
			{
				if (trn == pmr.ntren || trn == 0xffffff) pmr.mensajeRecibido(Mensajes.fromId(code));
			}
			if (code != 253) recibiendo = false;
		} catch (IOException e) {
			client = null;
			canalconectado = -1;
			recibiendo = false;
			e.printStackTrace();
		}
	}
}

package pmr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
	String ip;
	boolean enviando;
	boolean recibiendo;
	Client(PMR pmr)
	{
		this.pmr = pmr;
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
	    			if (token[0].trim().equalsIgnoreCase("ip"))
	    			{
	    				String val = token[1].trim();
	    				ip = val;
	    			}
				}
	    		line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ip == null) ip = JOptionPane.showInputDialog("IP a la que conectarse");
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
				client = new Socket(ip, pmr.canal + 48300);
				in = client.getInputStream();
				out = client.getOutputStream();
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
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write((byte)253);
		enviar(o.toByteArray());
		Input in=new Input();
		while(enviando) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		enviando = true;
		try {
			while(pmr.estado == Estado.ComunicacionEstablecida && pmr.descolgado)
			{
				out.write(1);
				out.write(in.getAudio());
			}
			out.write(0);
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
				if (trn == pmr.ntren || trn == 255*(256*256+256+1))
				{
					if (trn != pmr.ntren) pmr.mensaje_recibido = Mensajes.LlamadaGeneral;
					Output o = new Output();
					while(in.read()==1)
					{
						byte[] data = new byte[Input.BUFFERSIZE];
						int numread = 0;
						while(numread<Input.BUFFERSIZE)
							numread += in.read(data, numread, Input.BUFFERSIZE-numread);
						o.play(data);
					}
					pmr.descolgado = false;
					pmr.estado = Estado.Normal;
					pmr.mensaje_recibido = null;
					o.stop();
				}
			}
			else
			{
				if (trn == pmr.ntren) pmr.mensajeRecibido(Mensajes.fromId(code));
			}
		} catch (IOException e) {
			client = null;
			canalconectado = -1;
			e.printStackTrace();
		}
		recibiendo = false;
	}
}

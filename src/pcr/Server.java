package pcr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import pmr.PMR.Mensajes;
import streaming.Input;
import streaming.Output;

public class Server {
	List<Socket> sockets = new ArrayList<>();
	HashMap<Socket, OutputStream> outputs = new HashMap<>();
	PCR pcr;
	public synchronized void read()
	{
		for (Socket s : sockets)
		{
			try {
				InputStream in = s.getInputStream();
				if (in.available() >= 4)
				{
					int trn = in.read();
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
						pcr.textoRecibido(new String(data), trn);
					}
					else if (code == 254)
					{
						pcr.listatrenes.put(trn, System.currentTimeMillis());
					}
					else if (code == 253)
					{
						if (trn == pcr.hable_pm1)
						{
						}
						else if (trn == pcr.hable_pm2)
						{
							
						}
						else
						{
							Output o = new Output();
							while(in.read()==1)
							{
								for (Socket s2 : sockets)
								{
									try {
										InputStream in2 = s2.getInputStream();
										if (s2 == s || in2.available() < 4) continue;
										in2.mark(4);
										in2.read();
										in2.read();
										in2.read();
										if (in2.read() == Mensajes.Emergencia.getId())
										{
											pcr.hablando = false;
											in2.reset();
											break;
										}
										in2.reset();
									} catch(Exception e) {}
								}
								byte[] data = new byte[Input.BUFFERSIZE];
								int numread = 0;
								while(numread<Input.BUFFERSIZE)
									numread += in.read(data, numread, Input.BUFFERSIZE-numread);
								o.play(data);
							}
							pcr.hablando = false;
							o.stop();
						}
					}
					else
					{
						pcr.mensajeRecibido(Mensajes.fromId(code), trn);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	Server(PCR pcr)
	{
		this.pcr = pcr;
		new Thread(() -> {
			try {
				ServerSocket ss = new ServerSocket(48300+pcr.canal);
				while(true)
				{
					Socket s = ss.accept();
					outputs.put(s, s.getOutputStream());
					sockets.add(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			while(true)
			{
				read();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	boolean enviando=false;
	void writeFast(byte[] data)
	{
		List<Socket> old = new ArrayList<>();
		for (Socket s : sockets)
		{
			try {
				OutputStream out = outputs.get(s);
				out.write(data);
			} catch (IOException e) {
				synchronized(old)
				{
					old.add(s);
					e.printStackTrace();
				}
			}
		}
		for (Socket s : old)
		{
			sockets.remove(s);
			outputs.remove(s);
		}
	}
	void enviar(int tren, byte[] bytes)
	{
		if (enviando) return;
		enviando = true;
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write((byte)(tren/65536));
		o.write((byte)((tren/256)%256));
		o.write((byte)(tren%256));
		o.write(bytes, 0, bytes.length);
		byte[] b = o.toByteArray();
		List<Socket> old = new ArrayList<>();
		for (Socket s : sockets)
		{
			new Thread(() -> {
			try {
				OutputStream out = outputs.get(s);
				for (int i=0; i<b.length; i++)
				{
					out.write(b[i]);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				synchronized(old)
				{
					old.add(s);
					e.printStackTrace();
				}
			}}).start();
		}
		for (Socket s : old)
		{
			sockets.remove(s);
			outputs.remove(s);
		}
		enviando = false;
	}
	void enviarMensaje(Mensajes mensaje, int tren)
	{
		enviar(tren, new byte[]{(byte)mensaje.getId()});
	}
	void enviarTexto(String texto, int tren)
	{
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write((byte)255);
		byte[] tb = texto.getBytes();
		o.write((byte)tb.length);
		o.write(tb, 0, tb.length);
		enviar(tren, o.toByteArray());
	}
	public void enviarAudio(int tren)
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(enviando)
		{
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		enviar(tren, new byte[] {(byte)253});
		enviando = true;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Input in=new Input();
		while(pcr.hablando)
		{
			writeFast(new byte[] {1});
			writeFast(in.getAudio());
		}
		writeFast(new byte[] {0});
		enviando=false;
		in.stop();
	}
}

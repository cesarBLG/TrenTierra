package pcr;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
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
	Output audioOut;
	public void read(Socket s)
	{
		try {
			InputStream in = s.getInputStream();
			int trn = in.read();
			if (trn == -1) throw new EOFException();
			trn = in.read() + trn * 256;
			trn = in.read() + trn * 256;
			int code = in.read();
			if (code == -1) throw new EOFException();
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
				if (audioOut == null) audioOut = new Output();
				byte[] data = new byte[Input.BUFFERSIZE];
				int numread = 0;
				while(numread<Input.BUFFERSIZE)
					numread += in.read(data, numread, Input.BUFFERSIZE-numread);
				audioOut.play(data);
				int trn2 = -1;
				if (trn == pcr.hable_pm1) trn2 = pcr.hable_pm2;
				else if (trn == pcr.hable_pm2) trn2 = pcr.hable_pm1;
				if (trn2 != -1)
				{
				}
				else
				{
					
				}
			}
			else if (code == 252) {}
			else
			{
				pcr.mensajeRecibido(Mensajes.fromId(code), trn);
			}
			if (code != 253)
			{
				if (audioOut != null)
				{
					audioOut.stop();
					audioOut = null;
				}
			}
		} catch (EOFException e) {
			sockets.remove(s);
			outputs.remove(s);
		} catch (IOException e) {
			e.printStackTrace();
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
					new Thread(() -> {
						while(sockets.contains(s)) read(s);
					}).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		enviando = true;
		Input in=new Input();
		while(pcr.hablando)
		{
			writeFast(new byte[] {(byte)255,(byte)255,(byte)255,(byte)253});
			writeFast(in.getAudio());
		}
		writeFast(new byte[] {(byte)255,(byte)255,(byte)255,(byte)252});
		enviando=false;
		in.stop();
	}
}

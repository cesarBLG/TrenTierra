package pmr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.Timer;

import pmr.gui.BotonTT.TipoBoton;

public class Arduino
{
	PMR pmr;
	Socket s;
	OutputStream out;
	BufferedReader in;
	public static Socket getSocket()
	{
		Socket s = null;
		while(s==null)
		{
			String ip = "localhost";
			try
			{
				DatagramSocket ds = new DatagramSocket(null);
				ds.setBroadcast(true);
				ds.bind(new InetSocketAddress("0.0.0.0", 5091));
				ds.setSoTimeout(1000);
				byte[] buff = new byte[50];
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				ds.receive(packet);
				ip = packet.getAddress().getCanonicalHostName();
				ds.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				s = new Socket(ip, 5090);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if(s == null || !s.isConnected())
			{
				s = null;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return s;
	}
	void setup()
	{
		s = getSocket();
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = s.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		subscribe("tt::boton::*");
		while(true)
		{
			String s = readData();
			if (s == null) return;
			parse(s);
		}
	}
	public Arduino(PMR pmr)
	{
		this.pmr = pmr;
		new Thread(() -> {
			while (true)
			{
				setup();
				in = null;
				out = null;
				s = null;
			}
		}).start();
	}
	void parse(String s)
	{
		if(s==null) return;
		String[] spl = s.split("=");
		if (spl[0].startsWith("tt::boton::"))
		{
			if (spl[1].equals("1"))
			{
				for (TipoBoton t : TipoBoton.values())
				{
					if (spl[0].substring(11).equalsIgnoreCase(t.name())) pmr.pulsacion(t);
				}
			}
		}
	}
	static boolean matches(String topic, String var)
	{
		if (topic.startsWith("register(")) topic = topic.substring(9, topic.length()-1);
		if (topic.startsWith("get(")) topic = topic.substring(4, topic.length()-1);
		String[] t1 = topic.split("::");
		String[] t2 = var.split("::");
		for (int i=0; i<t1.length && i<t2.length; i++)
		{
			if (t1[i].equals("*")) return true;
			if (!t1[i].equals("+") && !t1[i].equals(t2[i])) break;
			if (i+1 == t1.length && t1.length == t2.length) return true;
		}
		return false;
	}
	void subscribe(String topic)
	{
		sendData("register("+topic+")");
	}
	public void sendData(String s)
	{
		if(out==null) return;
		s = s+'\n';
		char[] c = s.toCharArray();
		try {
			for(int i=0; i<c.length; i++) {
				out.write(c[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	String readData()
	{
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

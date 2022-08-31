package pmr.gui;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class BotonTT extends JButton {
	public enum TipoBoton
	{
		Empty,
		PidoHablar,
		EntroBanda,
		Bien,
		DetenidoSeñal,
		DetenidoTren,
		Megafonia,
		SigoMarcha,
		AveriaIF,
		Incidencia,
		ServicioExterior,
		Test,
		Interfono,
		PeticionVia,
		LlegadaPunto,
		SalidaPunto,
		PeticionManiobra,
		FinManiobra,
		Volumen,
		Emergencia,
		CLR,
		TXT,
		Confirmacion,
		N0,
		N1,
		N2,
		N3,
		N4,
		N5,
		N6,
		N7,
		N8,
		N9,
		ML,
		OnOff,
		Modo,
		Canal,
		Tren
	}
	static List<BotonTT> botones = new ArrayList<>();
	TipoBoton tipo;
	public BotonTT()
	{
		setBorder(null);
	}
	public BotonTT(TipoBoton t)
	{
		tipo = t;
		botones.add(this);
		setBorder(null);
		/*setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);*/
		URL location = getClass().getResource("/Content/Botones/"+tipo.name().toLowerCase()+".png");
    	ImageIcon ic = new ImageIcon(location);
    	Image img = ic.getImage();
    	Image newimg = img.getScaledInstance(img.getWidth(ic.getImageObserver())/2, img.getHeight(ic.getImageObserver())/2, java.awt.Image.SCALE_SMOOTH);
		setIcon(new ImageIcon(newimg));
	}
	public static void setPressedActions(PanelTT panel)
	{
		for (BotonTT b : botones) {
			b.addActionListener((arg0) -> {
				panel.pmr.pulsacion(b.tipo);
			});
		}
	}
	public static synchronized void playSound(final String url) {
		new Thread(() -> {
		try {
			Clip clip = AudioSystem.getClip();
			//read audio data from whatever source (file/classloader/etc.)
			InputStream audioSrc = BotonTT.class.getResourceAsStream("/Content/Sonido/" + url);
			//add buffer for mark/reset support
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			clip.open(audioStream);
			clip.start();
			Thread.sleep(1000);
			clip.addLineListener(e -> {
			    if (e.getType() == LineEvent.Type.STOP) {
			    	clip.drain();
					clip.close();
			      }
			    });
		} catch (Exception e) {
			e.printStackTrace();
		}}).start();
	}
}

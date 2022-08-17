package streaming;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Output {
    SourceDataLine speakers;
	public Output()
	{
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		try{
		    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
	        speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        speakers.open(format);
	        speakers.start();
	    }
		catch(LineUnavailableException e){
		    e.printStackTrace();
		}
	}
	public void play(byte[] audio)
	{
		speakers.write(audio, 0, audio.length);
	}
	public void stop()
	{
		speakers.drain();
		speakers.close();
	}
}

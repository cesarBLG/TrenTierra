package streaming;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Input {
	public static int BUFFERSIZE = 2048;
	TargetDataLine microphone;
	public Input()
	{
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		try{
		    microphone = AudioSystem.getTargetDataLine(format);
	
		    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		    microphone = (TargetDataLine)AudioSystem.getLine(info);
		    microphone.open(format);
		    microphone.start();
		}
		catch(LineUnavailableException e){
		    e.printStackTrace();
		}
	}
	public void stop()
	{
		if (microphone != null) microphone.close();
	}
	public byte[] getAudio()
	{
		if (microphone == null) return null;
	    byte[] data = new byte[BUFFERSIZE];
	    int bytesRead=0;
	    try{
	        while(bytesRead<BUFFERSIZE){
	        	bytesRead += microphone.read(data, bytesRead, data.length-bytesRead);
	        }
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	    return data;
	}
}

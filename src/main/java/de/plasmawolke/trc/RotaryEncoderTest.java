package de.plasmawolke.trc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class RotaryEncoderTest  {
	
	private static final File baseDir = new File("/home/pi/Desktop/VRC/");

	// Create gpio controller
	final GpioController gpio = GpioFactory.getInstance();

	// Pin pinA - CLK pin,
	// Pin pinB - DT pin
	// rotate(int i) - callback receiving -1 or 1

	final GpioPinDigitalInput inputA = gpio.provisionDigitalInputPin(
			RaspiPin.GPIO_00, "CLK", PinPullResistance.PULL_UP);

	final GpioPinDigitalInput inputB = gpio.provisionDigitalInputPin(
			RaspiPin.GPIO_01, "DT", PinPullResistance.PULL_UP);

	
	private Number value = 0;
	
	private Number currentChannel = 0;
	private String currentBand = "UKW";
	
	//private AudioDevice audioDevice;
	//private AdvancedPlayer player;
	private String currentMp3FileName = null;
	private Thread playerThread = null;
	
	private File nextMp3InPlayList = null;
	
	private PlaybackListener playbackListener = null;

	public RotaryEncoderTest() throws JavaLayerException {
		
		//determineNextMp3("UKW", "2");
		
		
		 playbackListener = new PlaybackListener() {
			
			@Override
			public void playbackStarted(PlaybackEvent arg0) {
				nextMp3InPlayList = determineNextMp3(currentBand, currentChannel.toString());
				super.playbackStarted(arg0);
			}
			
			@Override
			public void playbackFinished(PlaybackEvent arg0) {
				try {
					replaceSound(nextMp3InPlayList.getAbsolutePath());
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				super.playbackFinished(arg0);
			}
		};
		
		System.out.println("Encoder Value: "+ value);
		
		//audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
		
		
		inputA.addListener(new GpioPinListenerDigital() {

			int lastA;

			public synchronized void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent arg0) {
				int a = inputA.getState().getValue();
				int b = inputB.getState().getValue();
				if (lastA != a) {
					rotate(b == a ? -1 : 1);
					lastA = a;
				}
			}

		});
	}

	private void rotate(int i) {
		
		
		
		

		value = value.intValue() + i;
		// System.out.println(value);
		
		String fileToPlay = "mp3/langwelle.mp3";
		

		if (value.intValue() % 3 == 0) {
			
			int channel = value.intValue() / 3;
			
			System.out.println("Kanal " + channel);
			currentChannel = channel;
			
			
//			switch (channel) {
//			case 1:
//				fileToPlay = "mp3/01_BobDylanRadio/1 _2.01_Hello.mp3";		
//				break;
//			case 2:
//				fileToPlay = "mp3/01_BobDylanRadio/1 _2.23_Show_Joe.mp3";	
//				break;
//				
//			default:
//				fileToPlay = null;
//				break;
//			}
			
			File nextMp3 = determineNextMp3("UKW", String.valueOf(channel));
			
			if(nextMp3 != null){
				
			  fileToPlay = nextMp3.getAbsolutePath();
			}else{
				System.out.println("No next file found!");
			}
			
			System.out.println(fileToPlay);
					
			
			
			
			
			

		} else {
			System.out.println("Rauschen");
		    fileToPlay = "mp3/langwelle.mp3";
		}
		
		try {
			System.out.println("Wiedergabe von "+fileToPlay);
			replaceSound(fileToPlay);
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	private File determineNextMp3(String band, String channel){
		System.out.println("determineNextMp3("+band+", "+channel+")");
		
		File mp3 = null; 
		
		File dir = new File(baseDir,band+"/"+channel);
		
		String[] types = new String[]{"mp3"};
		
		Collection<File> mp3s =  FileUtils.listFiles(dir, types, true);
		
//		for (File file : mp3s) {
//			System.out.println(file.getName());
//		}
		
		Iterator<File> it = mp3s.iterator();
		
		boolean foundCurrent = false;
		File nextFile = null;
		while(it.hasNext()){
			
			nextFile = it.next();
			
			String next = nextFile.getAbsolutePath();
			System.out.println("Checking "+next);
			
			if("mp3/langwelle.mp3".equals(currentMp3FileName)){
				return nextFile;
			}
			
			if(next.equals(currentMp3FileName)){
				foundCurrent = true;
				continue;
			}
			if(foundCurrent){
				return nextFile;	
			}
			
		}
		return nextFile;
		
		
		
		
		
		
	}
	
	private synchronized void replaceSound(final String mp3FileName) throws JavaLayerException, IOException{
		
		if(mp3FileName == null){
			return;
		}
		
		if(mp3FileName.equals(currentMp3FileName)){
			return;
		}
		
		currentMp3FileName = mp3FileName;
		
		if(playerThread != null){
			playerThread.interrupt();
			//playerThread.
			
			System.out.println(playerThread.isInterrupted());
			
			try {
				playerThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Joined");
		}
		
		
//		if(player != null){
//			try {
//				player.stop();	
//			} catch (Exception e) {
//				System.out.println("Error while stopping player: "+e);
//			}
//			try {
//				player.close();	
//			} catch (Exception e) {
//				System.out.println("Error while closing player: "+e);
//			}
//			
//		}
		
		
		
		
		try {
			
			playerThread = new Thread("Thread("+mp3FileName+")") {
				
				AdvancedPlayer player;

				public void run() {
					try {
						player =  new AdvancedPlayer(getIs(mp3FileName), FactoryRegistry.systemRegistry().createAudioDevice());
						player.setPlayBackListener(playbackListener);
						player.play();
						
					} catch (JavaLayerException e) {
						System.err.println("JavaLayerException+" +e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if(player != null){
							System.out.println("Closing player");
							player.close();
						}
					}
				}
				
				@Override
				public void interrupt() {
					System.out.println(this + "is interrupted");
					try {
						player.stop();
					} catch (Exception e) {
						//System.out.println("Failed to stop player: "+e);
					}
					try {
						player.close();
					} catch (Exception e) {
						System.out.println("Failed to close player: "+e);
					}
					
					super.interrupt();
				}
			};
			playerThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static void createRadioChannelSourceFolders(){
		
		
		
		String[] bands = new String[]{"UKW","L","M","K","TA"};
		
		for (int i = 0; i < bands.length; i++) {
			File bandDir = new File(baseDir,bands[i]);
			for (int j = 0; j < 50; j++) {
				File channelDir = new File(bandDir,""+j);
				if(!channelDir.exists()){
					channelDir.mkdirs();
				}
			}
		}
		
	}

	public static void main(String[] args) throws JavaLayerException {
		
		createRadioChannelSourceFolders();

		System.out.println("<--Pi4J--> Rotary Encoder ... started.");
		
		
		new RotaryEncoderTest();

		while (true)
			;

	}
	
	private static InputStream getIs(String dateiname) throws IOException {
		return new BufferedInputStream(new FileInputStream(dateiname));
	}

}

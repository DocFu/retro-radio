package de.plasmawolke.trc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;



/**
 * 
 * @author Arne Schueler
 */
public class Radio {

	private final static Logger logger = LoggerFactory.getLogger(Radio.class);

	public static final File BASE_DIR = new File(System.getProperty("user.home") + "/TRC/");
	private static final File AUTH_INFO_FILE = new File(BASE_DIR, "auth-info.ser");
	private static final File CONFIG_FILE = new File(BASE_DIR, "config.xml");

	private GpioController gpio = null;

	

	

	/**
	 * Constructs the Application
	 */
	public Radio() {

		try {

			// Initialize config (bridge config, switches,...)
			init();
			
			// wire();

			

		} catch (Exception e) {
			logger.error("Construct failed", e);
		}
	}

	

	

	

	/**
	 * Sets up configuration
	 * @throws IOException
	 *             if files could not be created
	 * @throws JAXBException
	 */
	private void init() throws IOException, JAXBException {

		//logger.info("InetAdress: " + InetAddress.getLocalHost());

		logger.info("Intializing configuration...");

		if (!BASE_DIR.exists()) {
			BASE_DIR.mkdirs();
			logger.debug("Creating base directory " + BASE_DIR);
		}

		// try to get config from file
		// if config file exists, try to read and set config
		// else create a new file from a new CucarachaConfig instance
		
		/*
		if (CONFIG_FILE.exists()) {
			logger.info("Loading existing config file " + CONFIG_FILE);
			Unmarshaller unmarshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class })
					.createUnmarshaller();
			cfg = (CucarachaConfig) unmarshaller.unmarshal(CONFIG_FILE);

			logger.info("Loaded config file succesfully.");
		} else {
			logger.info("Creating new a config file " + CONFIG_FILE);
			cfg = new CucarachaConfig();

			CucarachaAccessory sampleAccessory1 = new CucarachaAccessory();
			sampleAccessory1.setType(AccessoryType.LIGHT);
			sampleAccessory1.setHapId(2);
			sampleAccessory1.setHapLabel("Baulicht");

			//sampleAccessory1.setGpioPowerStateWriterPin(0);
			//sampleAccessory1.setGpioPowerStateReaderPin(1);
			cfg.getAccessories().add(sampleAccessory1);

			CucarachaAccessory sampleAccessory2 = new CucarachaAccessory();
			sampleAccessory2.setType(AccessoryType.LIGHT);
			sampleAccessory2.setHapId(3);
			sampleAccessory2.setHapLabel("Rote Stimmung");
			//sampleAccessory2.setGpioPowerStateWriterPin(2);
			//sampleAccessory2.setGpioPowerStateReaderPin(3);

			cfg.getAccessories().add(sampleAccessory2);

			CucarachaAccessory sampleAccessory3 = new CucarachaAccessory();
			sampleAccessory3.setType(AccessoryType.LIGHT);
			sampleAccessory3.setHapId(4);
			sampleAccessory3.setHapLabel("Grüne Stimmung");
			//sampleAccessory2.setGpioPowerStateWriterPin(2);
			//sampleAccessory2.setGpioPowerStateReaderPin(3);

			cfg.getAccessories().add(sampleAccessory3);

			Marshaller marshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class }).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(cfg, CONFIG_FILE);

			logger.warn("Created new configuration [" + CONFIG_FILE
					+ "] with example values. Please edit the file according to your needs and restart!");
			System.exit(0);
		}

		logger.info("====== CONFIG BEGIN======");
		logger.info(cfg.print());
		logger.info("====== CONFIG END======");
*/
		logger.info("Intializing configuration done!");
	}

	/**
	 * Wire GPIO to HAP an vice versa by information from config
	 */
	private void wire() {
		logger.info("Wiring things...");

		boolean runningOnPi = System.getProperty("os.arch").startsWith("arm");

		if (runningOnPi) {
			gpio = GpioFactory.getInstance();
		} else {
			logger.warn("Wrong platform detected! Using GPIO Mock. Expect some Errors (NPEs)...");
			gpio = new MockGpioController();
		}

		
		logger.info("Wiring things done!");

	}

	

	
	private void deserialize() throws IOException {
		logger.info("Creating HomekitAuthInfo...");
		
		if (!AUTH_INFO_FILE.exists()) {
			AUTH_INFO_FILE.createNewFile();
			logger.info("Created new HomekitAuthInfo File " + AUTH_INFO_FILE);
		}

		if (AUTH_INFO_FILE.length() > 0) {
			Object x = SerializationUtils.deserialize(new FileInputStream(AUTH_INFO_FILE));
			logger.info("Restored HomekitAuthInfo from File " + AUTH_INFO_FILE);
		}

		


	}

	
	/**
	 * Runs {@link Cucaracha}
	 * @param args
	 */
	public static void main(String[] args) {
	
	
		Radio app = new Radio();
/*
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				app.updateDmx();
			}
		}, 30000, 5000);

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					logger.info("Updating remaining internet...");
					app.getRemainingInternetSensor().populate();
				} catch (Exception e) {
					logger.error("Error while updating remaining internet.", e);
				}
			}
		}, 30000, 1000 * 60);
		*/

	}

	

}

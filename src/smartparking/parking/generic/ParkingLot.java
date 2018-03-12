package smartparking.parking.generic;

import org.eclipse.californium.core.CoapServer;
import smartparking.common.LogFormatter;
import smartparking.common.PLInfo;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Superclass for all Parking Lot types
 */
public abstract class ParkingLot extends CoapServer {

	protected Logger LOG;
	protected PLInfoRes info_res;


	public ParkingLot(int port, String name) {
		super(port);
		LOG = Logger.getLogger(name);
		setupLogger(LOG);
	}

	public PLInfo getPLInfo() {
		return info_res.getPLInfo();
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

package smartparking.controldevices.eeserver;

import org.eclipse.californium.core.CoapServer;
import smartparking.common.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class ElectricalEventsServer extends CoapServer {

	private Logger LOG;
	private AddElectricalEvent addElectricalEvent;

	public ElectricalEventsServer(int port) {
		super(port);
		LOG=Logger.getLogger("ELECTRICAL_EVENTS_SERVER");
		setupLogger(LOG);

		addElectricalEvent = new AddElectricalEvent("add_event");
		add(addElectricalEvent);

		start();
	}


	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

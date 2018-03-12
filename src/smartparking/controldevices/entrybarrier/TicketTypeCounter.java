package smartparking.controldevices.entrybarrier;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by mirco on 12/05/17.
 */
public class TicketTypeCounter extends CoapResource {

	private Logger LOG;

	private EntryBarrier server;
	private String type;
	private int counter=0;


	public TicketTypeCounter(String name, EntryBarrier server) {
		super(name);
		LOG=Logger.getLogger(name.toUpperCase());
		setupLogger(LOG);

		this.server=server;
		this.type=name;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		LOG.info("Responding to GET with ticket count: "+getCounter());
		exchange.respond(CoAP.ResponseCode.CONTENT,Integer.toString(getCounter()), MediaTypeRegistry.TEXT_PLAIN);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void incrementCounter() {
		counter++;
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

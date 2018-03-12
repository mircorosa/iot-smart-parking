package smartparking.controldevices.exitbarrier;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class DismissTicket extends CoapResource {

	private Logger LOG;
	private ExitBarrier server;
	private int dismissedCounter;

	public DismissTicket(String name, ExitBarrier server) {
		super(name);
		LOG=Logger.getLogger(name.toUpperCase());
		setupLogger(LOG);

		dismissedCounter=0;
		this.server = server;
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		switch (server.dismissTicket(exchange.getRequestText())) {
			case 0:  //Valid
				LOG.info("Vehicle exited");
				dismissedCounter++;
				exchange.respond(CoAP.ResponseCode.VALID);
				break;
			case 1:  //Forbidden
				LOG.warning("Vehicle is not allowed to leave");
				exchange.respond(CoAP.ResponseCode.FORBIDDEN);
				break;
			case 2:  //Not Found
				LOG.severe("Ticket not found");
				exchange.respond(CoAP.ResponseCode.NOT_FOUND);
				break;
		}
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		LOG.info("Replying with dismissed ticket counter");
		exchange.respond(CoAP.ResponseCode.CONTENT,Integer.toString(dismissedCounter),MediaTypeRegistry.TEXT_PLAIN);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

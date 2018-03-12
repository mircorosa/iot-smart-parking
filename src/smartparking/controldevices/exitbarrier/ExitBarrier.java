package smartparking.controldevices.exitbarrier;

import org.eclipse.californium.core.CoapServer;
import smartparking.common.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by mirco on 12/05/17.
 */
public class ExitBarrier extends CoapServer {

	private Logger LOG;
	private DismissTicket dismissTicket;
	private ExitBCoapClient coapClient;

	public ExitBarrier(int port) {
		super(port);
		LOG=Logger.getLogger("EXIT_BARRIER");
		setupLogger(LOG);

		dismissTicket = new DismissTicket("dismiss_ticket",this);
		add(dismissTicket);

		coapClient = new ExitBCoapClient();

		start();
	}

	public int dismissTicket(String ticketCode) {
		return coapClient.dismissTicket(ticketCode);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

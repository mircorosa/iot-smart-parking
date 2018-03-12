package smartparking.controldevices.exitbarrier;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.LogFormatter;
import smartparking.common.Prefs;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class ExitBCoapClient extends CoapClient {

	private Logger LOG;

	public ExitBCoapClient() {
		super();
		LOG=Logger.getLogger("EXIT_B_COAP_CLIENT");
		setupLogger(LOG);

		this.setURI("127.0.0.1:"+ Prefs.PAYMENT_BOX_PORT+"/ticket_check");
	}

	public int dismissTicket(String ticketCode) {
		Request request = Request.newPut();
		request.setPayload(ticketCode);
		CoapResponse response = advanced(request);
		if(response.getCode().equals(CoAP.ResponseCode.VALID)) {
			LOG.info("Ticket dismissed");
			return 0;
		} else if (response.getCode().equals(CoAP.ResponseCode.FORBIDDEN)) {
			LOG.warning("Ticket not paid yet");
			return 1;
		}
		else if (response.getCode().equals(CoAP.ResponseCode.NOT_FOUND)) {
			LOG.severe("Ticket not found");
			return 2;
		}
		return -1;
	}


	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

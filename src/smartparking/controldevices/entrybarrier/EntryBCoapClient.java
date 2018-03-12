package smartparking.controldevices.entrybarrier;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.LogFormatter;
import smartparking.common.Prefs;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class EntryBCoapClient extends CoapClient {

	private Logger LOG;

	public EntryBCoapClient() {
		super();
		LOG=Logger.getLogger("ENTRY_B_COAP_CLIENT");
		setupLogger(LOG);
	}

	public void putStandardTicketToPaymentBox(String ticket) {
		this.setURI("127.0.0.1:"+ Prefs.PAYMENT_BOX_PORT+"/unpaid_lots");
		LOG.info("Sending ticket info...");
		Request request = Request.newPut();
		request.setURI(this.getURI());
		request.setPayload(ticket);
		CoapResponse response = advanced(request);
		if (response.getCode().equals(CoAP.ResponseCode.VALID)) LOG.info("Ticket info sent.");
		else  LOG.info("Ticket info not sent properly.");
	}


	public void putPrivateHandTicketIntoPaymentBox(String ticket) {
		this.setURI("127.0.0.1:"+ Prefs.PAYMENT_BOX_PORT+"/paid_lots");
		LOG.info("Sending private/hand ticket info...");
		Request request = Request.newPut();
		request.setURI(this.getURI());
		request.setPayload(ticket);
		CoapResponse response = advanced(request);
		if (response.getCode().equals(CoAP.ResponseCode.VALID)) LOG.info("Private/hand ticket info sent.");
		else  LOG.severe("Ticket info not sent properly.");
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

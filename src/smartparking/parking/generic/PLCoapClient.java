package smartparking.parking.generic;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.LogFormatter;
import smartparking.common.Prefs;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public abstract class PLCoapClient extends CoapClient {
	protected Logger LOG;

	public PLCoapClient(PLInfoRes infoRes) {
		super();
		LOG=Logger.getLogger(infoRes.getPLInfo().getName().toUpperCase()+"_COAP_CLIENT");
		setupLogger(LOG);
	}

	public boolean checkTicketValidity(String ticketCode) {
		this.setURI("127.0.0.1:"+ Prefs.PAYMENT_BOX_PORT+"/ticket_check");
		Request request = Request.newGet();
		OptionSet optionSet = new OptionSet();
		optionSet.addOption(new Option(15,ticketCode)); //Query option
		request.setOptions(optionSet);
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case VALID:
				LOG.info("Valid ticket");
				return true;
			case FORBIDDEN:
				LOG.warning("Ticket not paid");
				return false;
			case NOT_FOUND:
				LOG.severe("Ticket not found");
				return false;
			default:
				LOG.severe("Ticket not valid");
				return false;
		}
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

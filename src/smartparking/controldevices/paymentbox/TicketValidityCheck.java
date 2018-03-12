package smartparking.controldevices.paymentbox;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;
import smartparking.common.Ticket;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class TicketValidityCheck extends CoapResource {

	private Logger LOG;
	private PaymentBox server;

	public TicketValidityCheck(String name, PaymentBox server) {
		super(name);
		LOG=Logger.getLogger(name.toUpperCase());
		setupLogger(LOG);

		this.server=server;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		LOG.info("Ticket to check: "+exchange.getRequestOptions().getUriQueryString());
		Ticket ticket = server.getUnpaidTicketByCode(exchange.getRequestOptions().getUriQueryString());
		if(ticket!=null) {
			if(ticket.getParkingDate().isEmpty() || ticket.getLeavingDate().isEmpty())  //Valid ticket
				exchange.respond(CoAP.ResponseCode.VALID);
			else
				exchange.respond(CoAP.ResponseCode.FORBIDDEN);
		} else   //Unknown ticket
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		LOG.info("Ticket to check: "+exchange.getRequestText());
		Ticket ticket = server.getPaidTicketByCode(exchange.getRequestText());
		if(ticket!=null) {   //Valid ticket to be dismissed
			server.removePaidTicket(exchange.getRequestText());
			exchange.respond(CoAP.ResponseCode.VALID);
			return;
		}
		ticket = server.getUnpaidTicketByCode(exchange.getRequestText());
		if(ticket!=null) {   //Ticket not paid yet
			exchange.respond(CoAP.ResponseCode.FORBIDDEN);
			return;
		}
		//Otherwise, ticket is not in lists
		exchange.respond(CoAP.ResponseCode.NOT_FOUND);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

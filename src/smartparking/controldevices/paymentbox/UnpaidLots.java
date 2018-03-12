package smartparking.controldevices.paymentbox;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;
import smartparking.common.Ticket;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class UnpaidLots extends CoapResource {

	private Logger LOG;

	PaymentBox server;

	ArrayList<Ticket> tickets = new ArrayList<>();

	public UnpaidLots(String name, PaymentBox server) {
		super(name);
		LOG=Logger.getLogger("UNPAID_LOTS");
		setupLogger(LOG);

		this.server=server;
	}

	//Coap methods
	@Override
	public void handlePUT(CoapExchange exchange) {
		LOG.info(exchange.getRequestText());
		tickets.add(new Gson().fromJson(exchange.getRequestText(),Ticket.class));
		exchange.respond(CoAP.ResponseCode.VALID);
		LOG.info("List updated.");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(CoAP.ResponseCode.CONTENT,new Gson().toJson(tickets), MediaTypeRegistry.TEXT_PLAIN);
		LOG.info("Ticket list sent.");
	}

	public Ticket getTicketByCode(String ticketCode) {
		for (Ticket ticket : tickets)
			if(ticket.getCode().equals(ticketCode)) return ticket;
		return null;
	}

	public Ticket getTicketByLotName(String lotName) {
		for (Ticket ticket : tickets)
			if(ticket.getParkingLot().equals(lotName)) return ticket;
		return null;
	}

	public void addTicket(Ticket ticket) {
		tickets.add(ticket);
	}

	public void removeTicket(String ticketCode) {
		for (Ticket ticket : tickets)
			if(ticket.getCode().equals(ticketCode)) {
				tickets.remove(ticket);
				return;
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

package smartparking.controldevices.paymentbox;

import org.eclipse.californium.core.CoapServer;
import smartparking.common.LogFormatter;
import smartparking.common.Ticket;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by mirco on 12/05/17.
 */
public class PaymentBox extends CoapServer {

	private Logger LOG;

	private ParkingPayment parkingPayment;
	private PaidLots paidLots;
	private UnpaidLots unpaidLots;
	private TicketValidityCheck ticketValidityCheck;
	private PBCoapClient coapClient;

	public PaymentBox(int port) {
		super(port);
		LOG=Logger.getLogger("PAYMENT_BOX");
		setupLogger(LOG);

		parkingPayment = new ParkingPayment("parking_payment",this);
		add(parkingPayment);

		paidLots = new PaidLots("paid_lots",this);
		add(paidLots);

		unpaidLots = new UnpaidLots("unpaid_lots",this);
		add(unpaidLots);

		ticketValidityCheck = new TicketValidityCheck("ticket_check",this);
		add(ticketValidityCheck);

		//Parking observe relations setup
		coapClient = new PBCoapClient(this);

		start();
	}

	public Ticket getUnpaidTicketByCode(String ticketCode) {
		return unpaidLots.getTicketByCode(ticketCode);
	}

	public Ticket getUnpaidTicketByLotName(String lotName) {
		return unpaidLots.getTicketByLotName(lotName);
	}

	public Ticket getPaidTicketByCode(String ticketCode) {
		return paidLots.getTicketByCode(ticketCode);
	}

	public Ticket getPaidTicketByLotName(String lotName) {
		return paidLots.getTicketByLotName(lotName);
	}

	public void addPaidTicket(Ticket ticket) {
		paidLots.addTicket(ticket);
	}

	public void removePaidTicket(String ticketCode) {
		paidLots.removeTicket(ticketCode);
	}

	public void addUnpaidTicket(Ticket ticket) {
		unpaidLots.addTicket(ticket);
	}
	public void removeUnpaidTicket(String ticketCode) {
		unpaidLots.removeTicket(ticketCode);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

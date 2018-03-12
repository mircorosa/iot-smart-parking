package smartparking.controldevices.paymentbox;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;
import smartparking.common.Prefs;
import smartparking.common.Ticket;
import smartparking.common.types.LotType;
import smartparking.common.types.PaymentType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class ParkingPayment extends CoapResource {

	private Logger LOG;

	private HashMap<String,Double> prices = new HashMap<>();

	private PaymentBox server;

	public ParkingPayment(String name, PaymentBox server) {
		super(name);
		LOG=Logger.getLogger("PARKING_PAYMENT");
		setupLogger(LOG);

		initPrices();

		this.server=server;
	}

	//Coap methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Payload format: ticketCode:paymentMethod
		String[] splitPayload = exchange.getRequestText().split(":");
		Ticket ticket = server.getUnpaidTicketByCode(splitPayload[0]);
		if(ticket!=null && isPaymentMethodValid(splitPayload[1])) { //Process Payment
			if(!ticket.getLeavingDate().isEmpty()) {
				DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
				Date currentTime = Calendar.getInstance().getTime();
				ticket.setPaymentDate(dateFormat.format(currentTime));
				ticket.setPaymentMethod(splitPayload[1]);
				payAndUpdateTicket(ticket);

				exchange.respond(CoAP.ResponseCode.CONTENT,new Gson().toJson(ticket),MediaTypeRegistry.APPLICATION_JSON);
				LOG.info(new Gson().toJson(ticket));
				server.addPaidTicket(server.getUnpaidTicketByCode(splitPayload[0]));
				server.removeUnpaidTicket(splitPayload[0]);
			} else {
				LOG.warning("Not ready to pay");
				exchange.respond(CoAP.ResponseCode.FORBIDDEN);
			}
		} else if (server.getPaidTicketByCode(splitPayload[0])!=null) {
			LOG.warning("Ticket already paid.");
			exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
		} else {
			LOG.severe("Ticket not found.");
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);
		}
	}

	private void payAndUpdateTicket(Ticket ticket) {
		try {
			//For testing purposes, prices are considered and calculated €/second (to avoid waits)
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			Date parkDate = dateFormat.parse(ticket.getParkingDate());
			Date leavingDate = dateFormat.parse(ticket.getLeavingDate());

			int parkingTimeSec = (int)((leavingDate.getTime() - parkDate.getTime()) / 1000 % 60);
			ticket.setDwellTime(parkingTimeSec);
			ticket.setTotalCost(parkingTimeSec*prices.get(ticket.getParkingLot().split("_")[0]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private boolean isPaymentMethodValid(String selectedType) {
		for(PaymentType type : PaymentType.values()) {
			if(type.getPaymentType().equals(selectedType))
				return true;
		}
		return false;
	}

	private void initPrices() {
		prices.put(LotType.values()[0].getLotName(),Prefs.PRICES[0]);  //Normal
		prices.put(LotType.values()[2].getLotName(),Prefs.PRICES[1]);  //Expectant
		prices.put(LotType.values()[3].getLotName(),Prefs.PRICES[2]);  //Electric
		prices.put(LotType.values()[5].getLotName(),Prefs.PRICES[3]);  //Moto
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

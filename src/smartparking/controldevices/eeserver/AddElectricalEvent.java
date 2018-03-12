package smartparking.controldevices.eeserver;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.ElectricalEvent;
import smartparking.common.LogFormatter;
import smartparking.common.Ticket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class AddElectricalEvent extends CoapResource {

	private Logger LOG;
	private ArrayList<ElectricalEvent> events = new ArrayList<>();

	public AddElectricalEvent(String name) {
		super(name);
		LOG=Logger.getLogger("ADD_ELECTRICAL_EVENT");
		setupLogger(LOG);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		//Message format example: 1:ticketCode:plate
		String[] payload = exchange.getRequestText().split(":");
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Date currentTime = Calendar.getInstance().getTime();
		if(payload[0].equals("1")) {  //Parking event
			events.add(new ElectricalEvent(payload[1],payload[2],dateFormat.format(currentTime)));
			LOG.info("Electrical event added.");
			exchange.respond(CoAP.ResponseCode.VALID);
		} else {  //Unparking event
			ElectricalEvent event = findParkingEvent(payload[1],payload[2]);
			if(event!=null) {
				event.setLeavingDate(dateFormat.format(currentTime));
				LOG.info("Electrical event completed");
				exchange.respond(CoAP.ResponseCode.VALID);
			} else {
				LOG.warning("Parking event not found");
				exchange.respond(CoAP.ResponseCode.NOT_FOUND);
			}

		}
	}

	//Provides event list
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(CoAP.ResponseCode.CONTENT,new Gson().toJson(events), MediaTypeRegistry.APPLICATION_JSON);
	}

	private ElectricalEvent findParkingEvent(String ticketCode, String plate) {
		for(ElectricalEvent event : events)
			if(event.getTicketCode().equals(ticketCode) && event.getPlate().equals(plate) && !event.getParkingDate().isEmpty())
				return event;
		return null;
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

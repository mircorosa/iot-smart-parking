package smartparking.controldevices.entrybarrier;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;
import smartparking.common.Ticket;
import smartparking.common.types.LotType;
import smartparking.common.types.VehicleType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by mirco on 12/05/17.
 */
public class TicketEmitter extends CoapResource {

	private Logger LOG;

	private int ticketEmittedTotal;
	private EntryBarrier server;

	public TicketEmitter(String name, EntryBarrier server) {
		super(name);
		LOG=Logger.getLogger("TICKET_EMITTER");
		setupLogger(LOG);

		this.server=server;
	}

	//Coap methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Pattern for Electric vehicle: type:plate
		String[] payload = exchange.getRequestText().split(":");
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Date currentTime = Calendar.getInstance().getTime();
		LOG.info(payload[0]);
		if(typeIsOk(payload[0])) { //Checks for unknown types
			server.incrementTypeCounter(payload[0]);
			StringBuilder ticket_code = new StringBuilder()
					.append(payload[0].substring(0,3).toUpperCase()) //Clients send Enum string as identifier
					.append(String.format("%05d",server.getTypeCount(payload[0])));
			LOG.info("New ticket emitted: "+ticket_code.toString());

			//If is Electric, add plate number to the ticket
			String json = "";
			if (payload.length==1)
				json = new Gson().toJson(new Ticket(ticket_code.toString(),dateFormat.format(currentTime)));
			else if (payload[0].equals(VehicleType.ELECTRIC.getVehicleName()) && payload.length==2)
				json = new Gson().toJson(new Ticket(ticket_code.toString(),dateFormat.format(currentTime),payload[1]));
			else {
				LOG.severe("Error in ticket request");
				exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
			}

			if(!payload[0].equals(VehicleType.PRIVATE.getVehicleName()) && !payload[0].equals(VehicleType.HAND.getVehicleName()))   //Not private or hand
				server.putStandardTicketToPaymentBox(json);
			else  //Private and Hand ticket, managed as already paid
				server.putPrivateHandTicketToPaymentBox(json);
			exchange.respond(CoAP.ResponseCode.CONTENT,json,MediaTypeRegistry.APPLICATION_JSON);
			LOG.info("Json: "+json);
		}
		else {
			LOG.severe("Lot type not recognized");
			exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
		}
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		LOG.info("Responding to GET with total count: "+server.getTotalCount());
		exchange.respond(CoAP.ResponseCode.CONTENT,Integer.toString(server.getTotalCount()), MediaTypeRegistry.TEXT_PLAIN);

	}

	private boolean typeIsOk(String request) {
		for (LotType type : LotType.values())
			if(type.getLotName().equals(request)) return true;
		return false;
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

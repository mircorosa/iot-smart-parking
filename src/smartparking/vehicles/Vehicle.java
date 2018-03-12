package smartparking.vehicles;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.*;
import smartparking.common.types.PaymentType;
import smartparking.common.types.VehicleType;

import java.security.SecureRandom;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Abstract Class for vehicles
 */
public abstract class Vehicle extends CoapClient implements Runnable {
	protected Logger LOG;

	protected String name;
	protected VehicleType type;
	protected Ticket myTicket;
	protected PLInfo plInfo;
	protected SecureRandom sr;
	protected int parkingLotPort;

	public Vehicle(String name, VehicleType type) {
		super();
		LOG = Logger.getLogger(name.toUpperCase());
		setupLogger(LOG);

		sr = new SecureRandom();

		this.name=name;
		this.type=type;
	}

	public void enterParkingLot() throws VehicleException {
		setURI("127.0.0.1:"+ Prefs.ENTRY_BARRIER_PORT+"/emit_ticket");
		Request request = Request.newPost();
		request.setPayload(type.getVehicleName());
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case CONTENT:
				myTicket = new Gson().fromJson(response.getResponseText(),Ticket.class);
				LOG.info("Ticket acquired:\n"+response.getResponseText());
				break;
			case NOT_ACCEPTABLE:
				throw new VehicleException("Cannot enter the parking lot.");
		}
	}

	//Works for all parking lots except Private and Electric
	public void lotSearch() throws InterruptedException {
		int portRange = calculatePortRange();
		int port;
		PLInfo tryPLInfo;

		do {
			Thread.sleep(Prefs.PL_SEARCH_BASE_MILLS +sr.nextInt(Prefs.PL_SEARCH_RANGE_MILLS));
			port = Prefs.PL_STARTING_PORT+sr.nextInt(portRange);
			setURI("127.0.0.1:"+port+"/parking_lot");
			Request request = Request.newGet();
			tryPLInfo = new Gson().fromJson(advanced(request).getResponseText(),PLInfo.class);
			LOG.info("Trying to park at "+tryPLInfo.getName());
		} while(!tryPLInfo.getType().equals(type.getVehicleName()) || !tryPLInfo.getTicketCode().isEmpty() || tryPLInfo.isOccupied());

		plInfo=tryPLInfo;
		parkingLotPort=port;
	}

	public void park() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");
		Request request = Request.newPost();
		request.setPayload("1:"+myTicket.getCode());
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case VALID:
				LOG.info("Parked at "+plInfo.getName());
				break;
			case FORBIDDEN:
				throw new VehicleException("Parking lot occupied");   //It should never occur, as we check before parking
			case NOT_FOUND:
				throw new VehicleException("Error with ticket code");
			case BAD_REQUEST:
				throw new VehicleException("Unauthorized action");
		}
	}

	public void unpark() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");

		Request request = Request.newPost();
		request.setPayload("0:"+myTicket.getCode());
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case VALID:
				LOG.info("Unparked at "+plInfo.getName());
				break;
			case FORBIDDEN:
				throw new VehicleException("Parking lot occupied");   //It should never occur, as we check before parking
			case NOT_FOUND:
				throw new VehicleException("Error with ticket code");
			case NOT_ACCEPTABLE:
				throw new VehicleException("Invalid ticket");
			case BAD_REQUEST:
				throw new VehicleException("Unauthorized action");
		}
	}

	public void payTicket() throws VehicleException {
		setURI("127.0.0.1:"+ Prefs.PAYMENT_BOX_PORT+"/parking_payment");
		Request request = Request.newPost();
		request.setPayload(myTicket.getCode()+":"+getRandomPaymentMethod());
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case CONTENT:
				myTicket = new Gson().fromJson(response.getResponseText(),Ticket.class);
				LOG.info("Ticket paid:\n"+response.getResponseText());
				break;
			case FORBIDDEN:
				throw new VehicleException("Not ready to pay");
			case NOT_ACCEPTABLE:
				throw new VehicleException("Ticket already paid");
			case NOT_FOUND:
				throw new VehicleException("Ticket not found");
		}
	}

	public void exitParkingLot() throws VehicleException {
		setURI("127.0.0.1:"+ Prefs.EXIT_BARRIER_PORT+"/dismiss_ticket");
		Request request = Request.newPost();
		request.setPayload(myTicket.getCode());
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case VALID:
				LOG.info("Parking Lot leaved. Goodbye!");
				break;
			case FORBIDDEN:
				throw new VehicleException("Cannot leave the parking lot");
			case NOT_FOUND:
				throw new VehicleException("Ticket not found");
		}
	}


	public String getRandomPaymentMethod() {
		return PaymentType.values()[sr.nextInt(PaymentType.values().length)].getPaymentType();
	}

	public int calculatePortRange() {
		int ratio_size = 0;
		for(int type_ratio : Prefs.PL_RATIOS)
			ratio_size+=type_ratio;
		return ratio_size*Prefs.LEVEL_SIZE_FACTOR*Prefs.LEVELS;
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

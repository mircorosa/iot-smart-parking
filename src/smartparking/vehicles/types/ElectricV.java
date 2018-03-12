package smartparking.vehicles.types;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.Prefs;
import smartparking.common.Ticket;
import smartparking.common.VehicleException;
import smartparking.common.types.VehicleType;
import smartparking.vehicles.Vehicle;

public class ElectricV extends Vehicle {

	private String plate;

	public ElectricV(String name, VehicleType type) {
		super(name, type);
		plate = name+"-PLATE";
	}

	@Override
	public void run() {
		//Exceptions are used to stop the flow if some illegal action is performed (eg: 2 consecutive calls to park() or unpark())
		try {
			enterParkingLot();
			lotSearch();
			park();
			Thread.sleep(Prefs.PARKING_TIME_BASE_MILLS+sr.nextInt(Prefs.PARKING_TIME_RANGE_MILLS));
			unpark();
			Thread.sleep(Prefs.FROM_PL_TO_PB_BASE_MILLS+sr.nextInt(Prefs.FROM_PL_TO_PB_RANGE_MILLS));
			payTicket();
			Thread.sleep(Prefs.EXIT_BASE_MILLS +sr.nextInt(Prefs.EXIT_RANGE_MILLS));
			exitParkingLot();
		} catch (VehicleException e) {
			LOG.severe("Something goes wrong during operations: "+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enterParkingLot() throws VehicleException {
		setURI("127.0.0.1:"+ Prefs.ENTRY_BARRIER_PORT+"/emit_ticket");
		Request request = Request.newPost();
		request.setPayload(type.getVehicleName()+":"+plate);
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

	@Override
	public void park() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");
		Request request = Request.newPost();
		request.setPayload("1:"+myTicket.getCode()+":"+plate);
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

	@Override
	public void unpark() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");

		Request request = Request.newPost();
		request.setPayload("0:"+myTicket.getCode()+":"+plate);
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
}

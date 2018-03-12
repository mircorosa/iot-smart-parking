package smartparking.vehicles.types;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.PLInfo;
import smartparking.common.Prefs;
import smartparking.common.VehicleException;
import smartparking.common.types.VehicleType;
import smartparking.vehicles.Vehicle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrivateV extends Vehicle {

	private String secretCode;
	private String plName;

	public PrivateV(String name, VehicleType type) {
		super(name, type);
		String lcName = name.split("_")[0].toLowerCase()+"_"+sr.nextInt(Prefs.LEVELS)+"-"+sr.nextInt(Prefs.PL_RATIOS[1]*Prefs.LEVEL_SIZE_FACTOR);
		plName = lcName.substring(0, 1).toUpperCase() + lcName.substring(1);

		//For testing purposes, secret code is the hash of: lotName+" Secret Code"
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			String plainTextCode =plName+" Secret Code";
			messageDigest.update(plainTextCode.getBytes());
			secretCode= new String(messageDigest.digest());
			LOG.info("Secret code: "+secretCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
			Thread.sleep(Prefs.EXIT_BASE_MILLS +sr.nextInt(Prefs.EXIT_RANGE_MILLS));
			exitParkingLot();
		} catch (VehicleException e) {
			LOG.severe("Something goes wrong during operations: "+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void lotSearch() throws InterruptedException {
		int port = Prefs.PL_STARTING_PORT-1;
		PLInfo tryPLInfo;

		do {
			Thread.sleep(Prefs.PL_SEARCH_BASE_MILLS +sr.nextInt(Prefs.PL_SEARCH_RANGE_MILLS));
			port++;
			setURI("127.0.0.1:"+port+"/parking_lot");
			Request request = Request.newGet();
			tryPLInfo = new Gson().fromJson(advanced(request).getResponseText(),PLInfo.class);
			LOG.info("Trying to park at "+tryPLInfo.getName());
		} while(!tryPLInfo.getName().equals(plName));

		parkingLotPort = port;

		while(tryPLInfo.isOccupied()) {
			Request request = Request.newGet();
			tryPLInfo = new Gson().fromJson(advanced(request).getResponseText(),PLInfo.class);
			LOG.info("Waiting for parking lot");
			Thread.sleep(Prefs.PRIVATE_WAIT_INTERVAL_MILLS);
		}

		plInfo=tryPLInfo;
	}

	@Override
	public void park() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");
		Request request = Request.newPost();
		request.setPayload("1:"+myTicket.getCode()+":"+secretCode);
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
			case UNAUTHORIZED:
				throw new VehicleException("Wrong access code");
		}
	}

	@Override
	public void unpark() throws VehicleException {
		setURI("127.0.0.1:"+parkingLotPort+"/parking_lot");

		Request request = Request.newPost();
		request.setPayload("0:"+myTicket.getCode()+":"+secretCode);
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
			case UNAUTHORIZED:
				throw new VehicleException("Wrong access code");
		}
	}
}

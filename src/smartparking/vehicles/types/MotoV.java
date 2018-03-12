package smartparking.vehicles.types;

import smartparking.common.Prefs;
import smartparking.common.VehicleException;
import smartparking.common.types.VehicleType;
import smartparking.vehicles.Vehicle;

public class MotoV extends Vehicle {

	public MotoV(String name, VehicleType type) {
		super(name, type);
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
}

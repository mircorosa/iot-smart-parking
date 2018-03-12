package smartparking.vehicles;

import smartparking.common.types.VehicleType;
import smartparking.vehicles.types.*;

/**
 * Created by mirco on 17/05/17.
 */
public class VehicleFactory {
	public Vehicle createVehicle(VehicleType vehicleType, int number) {
		String vehicleName = vehicleType.getVehicleName()+"_"+number;
		switch (vehicleType) {
			case NORMAL:
				return new NormalV(vehicleName,vehicleType);
			case PRIVATE:
				return new PrivateV(vehicleName,vehicleType);
			case EXPECTANT:
				return new ExpectantV(vehicleName,vehicleType);
			case ELECTRIC:
				return new ElectricV(vehicleName,vehicleType);
			case HAND:
				return new HandV(vehicleName,vehicleType);
			case MOTO:
				return new MotoV(vehicleName,vehicleType);
		}
		return null;
	}
}

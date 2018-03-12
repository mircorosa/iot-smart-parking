package smartparking.parking;

import smartparking.common.types.LotType;
import smartparking.parking.generic.ParkingLot;
import smartparking.parking.lots.*;

/**
 * Created by mirco on 17/05/17.
 */
public class ParkingLotFactory {
	public ParkingLot createParkingLot(LotType lotType, int floor, int number, int port) {
		String lotName = lotType.getLotName()+"_"+floor+"-"+number;
		switch (lotType) {
			case NORMAL:
				return new NormalPL(lotName,lotType.getLotName(),floor,number,port);
			case PRIVATE:
				return new PrivatePL(lotName,lotType.getLotName(),floor,number,port);
			case EXPECTANT:
				return new ExpectantPL(lotName,lotType.getLotName(),floor,number,port);
			case ELECTRIC:
				return new ElectricPL(lotName,lotType.getLotName(),floor,number,port);
			case HAND:
				return new HandPL(lotName,lotType.getLotName(),floor,number,port);
			case MOTO:
				return new MotoPL(lotName,lotType.getLotName(),floor,number,port);
		}
		return null;
	}
}

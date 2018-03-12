package smartparking.parking.lots;

import smartparking.parking.inforesources.PLElectricInfoRes;
import smartparking.parking.generic.ParkingLot;

/**
 * Created by mirco on 17/05/17.
 */
public class ElectricPL extends ParkingLot {

	public ElectricPL(String name, String type, int level, int number, int port) {
		super(port,name);

		info_res = new PLElectricInfoRes(name,type,level,number);
		add(info_res);

		start();
		LOG.info("Server started on port "+port+".");
	}
}

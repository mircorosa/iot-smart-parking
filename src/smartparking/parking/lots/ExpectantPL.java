package smartparking.parking.lots;

import smartparking.parking.inforesources.PLStandardInfoRes;
import smartparking.parking.generic.ParkingLot;

/**
 * Created by mirco on 17/05/17.
 */
public class ExpectantPL extends ParkingLot {

	public ExpectantPL(String name, String type, int level, int number, int port) {
		super(port,name);

		info_res = new PLStandardInfoRes(name,type,level,number);
		add(info_res);

		start();
		LOG.info("Server started on port "+port+".");
	}
}

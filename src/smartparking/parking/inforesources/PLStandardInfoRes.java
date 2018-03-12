package smartparking.parking.inforesources;

import com.google.gson.Gson;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.PLInfo;
import smartparking.parking.generic.PLInfoRes;
import smartparking.parking.coapclients.PLStandardCoapClient;

/**
 * Generic resource that provides info about the parking lot
 */
public class PLStandardInfoRes extends PLInfoRes {

	private PLStandardCoapClient coapClient;

	public PLStandardInfoRes(String name, String type, int level, int number) {
		super(name,type,level,number);

		coapClient = new PLStandardCoapClient(this);
	}

	//Coap methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Message format example: 1:ticketCode
		String[] payload = exchange.getRequestText().split(":");
		if(payload.length==2) {
			if((Integer.valueOf(payload[0])==1 && plInfo.isOccupied()) || (Integer.valueOf(payload[0])==0 && !plInfo.isOccupied())) {
				exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
			} else if(coapClient.checkTicketValidity(payload[1])) {   //Checks from PB's ticket list
				if(!plInfo.isOccupied()) {
					plInfo.occupyParkingLot(payload[1]);
					changed();
					exchange.respond(CoAP.ResponseCode.VALID);   //Parked
				} else {
					if(plInfo.getTicketCode().equals(payload[1])) {
						plInfo.freeParkingLot();
						changed();
						exchange.respond(CoAP.ResponseCode.VALID);  //Parking lot freed
					} else {
						exchange.respond(CoAP.ResponseCode.FORBIDDEN);   //Parking lot occupied
					}
				}
			} else
				exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
		} else
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);   //No ticket code
	}

}

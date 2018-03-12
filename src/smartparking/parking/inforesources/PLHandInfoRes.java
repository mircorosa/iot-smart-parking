package smartparking.parking.inforesources;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.parking.coapclients.PLStandardCoapClient;
import smartparking.parking.generic.PLInfoRes;

/**
 * Generic resource that provides info about the parking lot
 */
public class PLHandInfoRes extends PLInfoRes {

	private PLStandardCoapClient coapClient;

	public PLHandInfoRes(String name, String type, int level, int number) {
		super(name,type,level,number);
	}

	//Coap methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Message format example: 1:ticketCode
		String[] payload = exchange.getRequestText().split(":");
		if(payload.length==2) {
			if((Integer.valueOf(payload[0])==1 && plInfo.isOccupied()) || (Integer.valueOf(payload[0])==0 && !plInfo.isOccupied())) {
				exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
			} else {
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
			}
		} else
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);   //No ticket code





//	public void handlePOST(CoapExchange exchange) {
//		//Message format example: 1:ticketCode
//		String[] payload = exchange.getRequestText().split(":");
//		if((Integer.valueOf(payload[0])==1 && plInfo.isOccupied()) || (Integer.valueOf(payload[0])==0 && !plInfo.isOccupied())) {
//			exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
//		} else if(coapClient.checkTicketValidity(payload[1])) {   //Checks from PB's ticket list
//			if(!plInfo.isOccupied() && payload.length==2) {
//				plInfo.occupyParkingLot(payload[1]);
//				changed();
//				exchange.respond(CoAP.ResponseCode.VALID);   //Parked
//			} else {
//				if(payload.length!=2) {
//					exchange.respond(CoAP.ResponseCode.NOT_FOUND);   //No ticket code
//				} else if(plInfo.getTicketCode().equals(payload[1])) {
//					plInfo.freeParkingLot();
//					changed();
//					exchange.respond(CoAP.ResponseCode.VALID);  //Parking lot freed
//				} else {
//					exchange.respond(CoAP.ResponseCode.FORBIDDEN);   //Parking lot occupied
//				}
//			}
//		} else
//			exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
	}

}

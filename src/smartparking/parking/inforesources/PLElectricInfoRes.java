package smartparking.parking.inforesources;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.parking.coapclients.PLElectricCoapClient;
import smartparking.parking.generic.PLInfoRes;

public class PLElectricInfoRes extends PLInfoRes {

	private PLElectricCoapClient coapClient;

	public PLElectricInfoRes(String name, String type, int level, int number) {
		super(name, type, level, number);

		coapClient = new PLElectricCoapClient(this);
	}

	//CoAP Methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Message format example: 1:ticketCode:plate
		String[] payload = exchange.getRequestText().split(":");
		if(payload.length==3) {
			if((Integer.valueOf(payload[0])==1 && plInfo.isOccupied()) || (Integer.valueOf(payload[0])==0 && !plInfo.isOccupied())) {
				exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
			} else if(coapClient.checkTicketValidity(payload[1])) {   //Checks from PB's ticket list
				if(!plInfo.isOccupied()) {
					plInfo.occupyParkingLot(payload[1]);
					plInfo.setPlate(payload[2]);
					changed();
					coapClient.postElectricalEvent(exchange.getRequestText());
					exchange.respond(CoAP.ResponseCode.VALID);   //Parked
				} else {
					if(plInfo.getTicketCode().equals(payload[1])) {
						plInfo.freeParkingLot();
						plInfo.emptyPlate();
						changed();
						coapClient.postElectricalEvent(exchange.getRequestText());
						exchange.respond(CoAP.ResponseCode.VALID);  //Parking lot freed
					} else {
						exchange.respond(CoAP.ResponseCode.FORBIDDEN);   //Parking lot occupied
					}
				}
			} else
				exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE);
		} else
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);   //No ticket code or plate
	}
}

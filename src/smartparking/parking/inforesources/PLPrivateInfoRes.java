package smartparking.parking.inforesources;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.parking.generic.PLInfoRes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PLPrivateInfoRes extends PLInfoRes {

	private String secretCode;

	public PLPrivateInfoRes(String name, String type, int level, int number) {
		super(name, type, level, number);

		//For testing purposes, secret code is the hash of: lotName+" Secret Code"
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			String plainTextCode = name+" Secret Code";
			messageDigest.update(plainTextCode.getBytes());
			secretCode= new String(messageDigest.digest());
			LOG.info("Secret code: "+secretCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	//CoAP Methods
	@Override
	public void handlePOST(CoapExchange exchange) {
		//Message format example: 1:ticketCode:secretCode
		String[] payload = exchange.getRequestText().split(":");

		if(payload.length==3) {
			if(payload[2].equals(secretCode)) { //Code confirmed
				LOG.info("Code confirmed - access granted");
				if((Integer.valueOf(payload[0])==1 && plInfo.isOccupied()) || (Integer.valueOf(payload[0])==0 && !plInfo.isOccupied()))
					exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
				else {
					if(!plInfo.isOccupied()) {
						plInfo.occupyParkingLot(payload[1]);
						changed();
						exchange.respond(CoAP.ResponseCode.VALID);   //Parked
					} else {
						if (plInfo.getTicketCode().equals(payload[1])) {
							plInfo.freeParkingLot();
							changed();
							exchange.respond(CoAP.ResponseCode.VALID);  //Parking lot freed
						} else {
							exchange.respond(CoAP.ResponseCode.FORBIDDEN);   //Parking lot occupied
						}
					}
				}
			} else {
				LOG.warning("Wrong code - not allowed to park");
				exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);   //Parking lot occupied
			}
		} else
			exchange.respond(CoAP.ResponseCode.NOT_FOUND);   //No ticket code or secret code
	}

}

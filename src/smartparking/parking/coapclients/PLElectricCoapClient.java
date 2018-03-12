package smartparking.parking.coapclients;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import smartparking.common.Prefs;
import smartparking.parking.generic.PLCoapClient;
import smartparking.parking.inforesources.PLElectricInfoRes;
import smartparking.parking.inforesources.PLPrivateInfoRes;

public class PLElectricCoapClient extends PLCoapClient {


	public PLElectricCoapClient(PLElectricInfoRes infoRes) {
		super(infoRes);
	}

	public void postElectricalEvent(String requestText) {
		this.setURI("127.0.0.1:"+ Prefs.ELECTRICAL_EVENTS_SERVER_PORT+"/add_event");
		Request request = Request.newPost();
		request.setPayload(requestText);
		CoapResponse response = advanced(request);

		switch (response.getCode()) {
			case VALID:
				LOG.info("Event updated.");
				return;
			case NOT_FOUND:
				LOG.severe("Event not found");
				return;
			default:
				LOG.severe("Error on updating electrical event");
		}
	}
}

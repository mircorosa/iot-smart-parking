package smartparking.parking.generic;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import smartparking.common.LogFormatter;
import smartparking.common.PLInfo;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public abstract class PLInfoRes extends CoapResource {

	protected Logger LOG;
	protected PLInfo plInfo;

	public PLInfoRes(String name, String type, int level, int number) {
		super("parking_lot");
		setObservable(true);
		LOG=Logger.getLogger(name+"_INFO_RES");
		setupLogger(LOG);

		plInfo = new PLInfo(name,type,level,number);
	}

	public synchronized PLInfo getPLInfo() {
		return plInfo;
	}

	@Override
	public synchronized void handleGET(CoapExchange exchange) {
		exchange.respond(CoAP.ResponseCode.CONTENT,new Gson().toJson(getPLInfo()), MediaTypeRegistry.APPLICATION_JSON);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

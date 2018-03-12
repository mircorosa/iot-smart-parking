package smartparking.controldevices.entrybarrier;

import org.eclipse.californium.core.CoapServer;
import smartparking.common.LogFormatter;
import smartparking.common.types.LotType;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by mirco on 12/05/17.
 */
public class EntryBarrier extends CoapServer {

	private Logger LOG;

	private TicketEmitter emitter;
	private EntryBCoapClient coapClient;
	private ArrayList<TicketTypeCounter> counters = new ArrayList<>();


	public EntryBarrier(int port) {
		super(port);
		LOG=Logger.getLogger("ENTRY_BARRIER");
		setupLogger(LOG);

		emitter = new TicketEmitter("emit_ticket",this);
		add(emitter);

		for (LotType type : LotType.class.getEnumConstants()) {
			TicketTypeCounter counter = new TicketTypeCounter(type.getLotName(),this);
			counters.add(counter);
			add(counter);
		}

		coapClient = new EntryBCoapClient();

		start();
		LOG.info("Entry barrier started.");
	}

	public int getTypeCount(String type) {
		for(TicketTypeCounter typeCounter : counters)
			if(typeCounter.getType().equals(type))
				return typeCounter.getCounter();
		return -1;
	}

	public int getTotalCount() {
		int total=0;
		for(TicketTypeCounter typeCounter : counters)
			total+=typeCounter.getCounter();
		return total;
	}

	public void incrementTypeCounter(String type) {
		for(TicketTypeCounter typeCounter : counters)
				if(typeCounter.getType().equals(type)) {
				typeCounter.incrementCounter();
				return;
			}
	}

	public void putStandardTicketToPaymentBox(String ticket) {
		coapClient.putStandardTicketToPaymentBox(ticket);
	}

	public void putPrivateHandTicketToPaymentBox(String ticket) {
		coapClient.putPrivateHandTicketIntoPaymentBox(ticket);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}
}

package smartparking.controldevices.paymentbox;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import smartparking.common.LogFormatter;
import smartparking.common.PLInfo;
import smartparking.common.Prefs;
import smartparking.common.Ticket;
import smartparking.common.types.LotType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class PBCoapClient extends CoapClient {

	private Logger LOG;
	PaymentBox server;

	private ArrayList<ParkingLotRecord> parkings = new ArrayList<>();

	public PBCoapClient(PaymentBox server) {
		super();
		LOG=Logger.getLogger("PB_COAP_CLIENT");
		setupLogger(LOG);
		this.server=server;

		initializeParkings();
	}

	public void initializeParkings() {
		//Lot identification name: type_level-number
		int port_number = 0;
		for (int levelNumber = 0; levelNumber < Prefs.LEVELS; levelNumber++) {
			for (LotType type : LotType.values()) {
				for (int i = 0; i < Prefs.PL_RATIOS[type.ordinal()] * Prefs.LEVEL_SIZE_FACTOR; i++) {
					final PLInfo plInfo = new PLInfo();
					this.setURI("127.0.0.1:"+(Prefs.PL_STARTING_PORT+port_number)+"/parking_lot");

					CoapHandler coapHandler = new CoapHandler() {
						//synchronized to avoid data inconsistency
						@Override
						public synchronized void onLoad(CoapResponse response) {
							//Updates received are right (errors handled directly by parking lot)
							PLInfo info = new Gson().fromJson(response.getResponseText(),PLInfo.class);

							if(isAnUpdate(info)) {  //To avoid 60-second refreshes of observing
								plInfo.setName(info.getName());
								plInfo.setType(info.getType());
								plInfo.setLevel(info.getLevel());
								plInfo.setNumber(info.getNumber());
								plInfo.setTicketCode(info.getTicketCode());
								plInfo.setPlate(info.getPlate());
								LOG.info("Received update: "+plInfo.getName()+" "+plInfo.getTicketCode());

								//Updating ticket time
								if(!plInfo.getTicketCode().isEmpty()) {   //Someone Parked
									Ticket ticketParkUpdate = (!plInfo.getType().equals(LotType.PRIVATE.getLotName()) && !plInfo.getType().equals(LotType.HAND.getLotName())) ? server.getUnpaidTicketByCode(plInfo.getTicketCode()) : server.getPaidTicketByCode(plInfo.getTicketCode());
									if(ticketParkUpdate!=null && ticketParkUpdate.getParkingDate().isEmpty()) {
										DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
										Date currentTime = Calendar.getInstance().getTime();
										ticketParkUpdate.setParkingDate(dateFormat.format(currentTime));
										ticketParkUpdate.setParkingLot(plInfo.getName());
										LOG.info(new Gson().toJson(ticketParkUpdate));
									} else
										LOG.warning("You already parked");
								} else { //Someone leaved
									Ticket ticketLeaveUpdate = (!plInfo.getType().equals(LotType.PRIVATE.getLotName()) && !plInfo.getType().equals(LotType.HAND.getLotName())) ? server.getUnpaidTicketByLotName(plInfo.getName()) : server.getPaidTicketByLotName(plInfo.getName());
									if(ticketLeaveUpdate!=null && ticketLeaveUpdate.getLeavingDate().isEmpty()) {
										DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
										Date currentTime = Calendar.getInstance().getTime();
										ticketLeaveUpdate.setLeavingDate(dateFormat.format(currentTime));
										LOG.info(new Gson().toJson(ticketLeaveUpdate));
									} else if (ticketLeaveUpdate!=null)
										LOG.warning("You already leaved the parking lot");
								}

							} else LOG.info("60 second refresh message");
						}

						@Override
						public void onError() {
							LOG.severe("Something has gone wrong handling observing");

						}
					};
					CoapObserveRelation observeRelation = observe(coapHandler);

					parkings.add(new ParkingLotRecord(plInfo,observeRelation));
					port_number++;
				}
			}
		}

	}

	private boolean isAnUpdate(PLInfo info) {
		for(ParkingLotRecord record : parkings) {
			if(record.getInfo().getName().equals(info.getName()) && record.getInfo().getTicketCode().equals(info.getTicketCode()))
				return false;
		}
		return true;
	}

	private void prettyPrintScenario() {
		String log = "## SMART PARKING SCENARIO ##\n";
		for (int level = 0; level < Prefs.LEVELS; level++) {
			log+="--- LEVEL "+level+" ---\n";
			for(LotType lotType : LotType.values()) {
				for(ParkingLotRecord lot : parkings) {
					if(lot.getInfo().getLevel()==level && lot.getInfo().getType().equals(lotType.getLotName())) {
						log += lot.getInfo().getName();
						log += lot.getInfo().isOccupied() ? "[BUSY]  " : "[FREE]  ";
					}
				}
				log+="\n";
			}
		}
		LOG.info(log);
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

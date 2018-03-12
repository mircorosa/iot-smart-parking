package smartparking;

import smartparking.common.Prefs;
import smartparking.common.types.LotType;
import smartparking.controldevices.eeserver.ElectricalEventsServer;
import smartparking.controldevices.entrybarrier.EntryBarrier;
import smartparking.controldevices.exitbarrier.ExitBarrier;
import smartparking.controldevices.paymentbox.PaymentBox;
import smartparking.parking.ParkingLotFactory;
import smartparking.common.LogFormatter;
import smartparking.parking.generic.ParkingLot;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Smart Parking scenario simulator
 */
public class SmartParking {

	private Logger LOG;

	ParkingLotFactory lotFactory = new ParkingLotFactory();
	ArrayList<ParkingLot> parkingLots = new ArrayList<>();

	EntryBarrier entryBarrier;
	PaymentBox paymentBox;
	ExitBarrier exitBarrier;
	ElectricalEventsServer electricalEventsServer;


	public SmartParking() {
		LOG=Logger.getLogger("SMART_PARKING");
		setupLogger(LOG);

		initializeParkingLots();
		initializeSystems();

		prettyPrintScenario();
	}

	private void initializeSystems() {
		LOG.info("Initializing Access and Payment systems...");
		entryBarrier = new EntryBarrier(Prefs.ENTRY_BARRIER_PORT);
		paymentBox = new PaymentBox(Prefs.PAYMENT_BOX_PORT);
		exitBarrier = new ExitBarrier(Prefs.EXIT_BARRIER_PORT);
		electricalEventsServer = new ElectricalEventsServer(Prefs.ELECTRICAL_EVENTS_SERVER_PORT);
	}

	private void initializeParkingLots() {
		for (int levelNumber = 0; levelNumber < Prefs.LEVELS; levelNumber++) {
			generateLevel(levelNumber);
		}
	}

	//Lot identification name: type_level-number
	private void generateLevel(int levelNumber) {
		for (LotType type : LotType.values()) {
			for (int i = 0; i < Prefs.PL_RATIOS[type.ordinal()] * Prefs.LEVEL_SIZE_FACTOR; i++) {
				parkingLots.add(lotFactory.createParkingLot(type,levelNumber,i,Prefs.PL_STARTING_PORT+parkingLots.size()));
			}
		}
	}

	private void prettyPrintScenario() {
		String log = "## SMART PARKING SCENARIO ##\n";
		for (int level = 0; level < Prefs.LEVELS; level++) {
			log+="--- LEVEL "+level+" ---\n";
			for(LotType lotType : LotType.values()) {
				for(ParkingLot lot : parkingLots) {
					if(lot.getPLInfo().getLevel()==level && lot.getPLInfo().getType().equals(lotType.getLotName())) {
						log += lot.getPLInfo().getName();
						log += lot.getPLInfo().isOccupied() ? "[BUSY]  " : "[FREE]  ";
					}
				}
				log+="\n";
			}
		}
		LOG.info(log);
	}


	public static void main(String[] args) {
		SmartParking smartParking = new SmartParking();


	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

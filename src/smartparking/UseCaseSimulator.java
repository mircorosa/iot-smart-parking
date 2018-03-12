package smartparking;

import smartparking.common.LogFormatter;
import smartparking.common.Prefs;
import smartparking.common.types.VehicleType;
import smartparking.vehicles.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Simulator of a real use case.
 */
public class UseCaseSimulator {

	private Logger LOG;
	private SecureRandom sr;
	private int probs[], type_counters[];
	private VehicleFactory vehicleFactory = new VehicleFactory();
	private ArrayList<Vehicle> parkingVehicles = new ArrayList<>();

	private UseCaseSimulator() {
		LOG=Logger.getLogger("SIMULATOR");
		setupLogger(LOG);
		sr = new SecureRandom();

		//Counters initialization
		type_counters = new int[VehicleType.values().length];
		Arrays.fill(type_counters,0);

		generateTypeProbabilityArray();

		if(Prefs.VEHICLES_PROBABILITY_RATIOS.length!=VehicleType.values().length)
			LOG.severe("***Probability array size does not match the number of vehicle types *** ");

		startSimulation();
	}

	private void startSimulation() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("##### PARKING LOT USE CASE SIMULATOR #####\nInsert the number of vehicles you want to simulate: ");
		int numberOfVehicles = scanner.nextInt();

		ExecutorService simExec = Executors.newFixedThreadPool(numberOfVehicles);
		for (int i = 0; i < numberOfVehicles; i++) {
			int randomType = probs[sr.nextInt(probs.length)];
			simExec.submit(vehicleFactory.createVehicle(VehicleType.values()[randomType],type_counters[randomType]));
			LOG.info("New vehicle arrived: "+VehicleType.values()[randomType]+"_"+type_counters[randomType]);
			type_counters[randomType]++;

			try {
				Thread.sleep(Prefs.VEHICLES_BASE_DELAY_MILLS +sr.nextInt(Prefs.VEHICHLES_DELAY_RANGE_MILLS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOG.info("All vehicles have been released.");
		simExec.shutdown();
		try {
			simExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void generateTypeProbabilityArray() {
		int probs_size = 0;
		for(int type_prob : Prefs.VEHICLES_PROBABILITY_RATIOS)
			probs_size+=type_prob;
		probs = new int[probs_size];

		//Reusing probs_size as index
		for (int i = 0; i < Prefs.VEHICLES_PROBABILITY_RATIOS.length; i++) {
			for (int j = 0; j < Prefs.VEHICLES_PROBABILITY_RATIOS[i]; j++) {
				probs[probs_size-1]=i;
				probs_size--;
			}
		}
	}

	public static void main(String[] args) {
		UseCaseSimulator simulator = new UseCaseSimulator();
	}

	private void setupLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

}

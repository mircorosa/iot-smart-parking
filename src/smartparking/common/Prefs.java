package smartparking.common;

/**
 * Created by mirco on 12/05/17.
 */
public class Prefs {

	/* SCENARIO GENERATION SETTINGS */
	public static int LEVELS = 1;
	public static int LEVEL_SIZE_FACTOR = 1;
	public static int PL_RATIOS[] = {
			/*NORMAL*/      5,
			/*PRIVATE*/     2,
			/*EXPECTANT*/   1,
			/*ELECTRIC*/    2,
			/*HAND*/        1,
			/*MOTO*/        2
	};
	public static double PRICES[] = {
			/*NORMAL*/      1.5,
			/*EXPECTANT*/   1.0,
			/*ELECTRIC*/    2.0,
			/*MOTO*/        1.0
	};


	/* SIMULATOR SETTINGS */
	public static int VEHICLES_BASE_DELAY_MILLS = 2000;
	public static int VEHICHLES_DELAY_RANGE_MILLS = 1000;
	public static int VEHICLES_PROBABILITY_RATIOS[] = {
			/*NORMAL*/      5,
			/*PRIVATE*/     2,
			/*EXPECTANT*/   1,
			/*ELECTRIC*/    2,
			/*HAND*/        1,
			/*MOTO*/        2
	};
	public static int PL_SEARCH_BASE_MILLS = 500;
	public static int PL_SEARCH_RANGE_MILLS = 100;
	public static int PARKING_TIME_BASE_MILLS = 5000;
	public static int PARKING_TIME_RANGE_MILLS = 5000;
	public static int PRIVATE_WAIT_INTERVAL_MILLS = 1000;
	public static int FROM_PL_TO_PB_BASE_MILLS = 700;
	public static int FROM_PL_TO_PB_RANGE_MILLS = 300;
	public static int EXIT_BASE_MILLS = 700;
	public static int EXIT_RANGE_MILLS = 300;


	/* LOCAL NETWORKING SCENARIO SETTINGS */
	public static int ENTRY_BARRIER_PORT = 10000;
	public static int EXIT_BARRIER_PORT = 10001;
	public static int PAYMENT_BOX_PORT = 10002;
	public static int ELECTRICAL_EVENTS_SERVER_PORT = 10003;

	public static int PL_STARTING_PORT = 10010;



}

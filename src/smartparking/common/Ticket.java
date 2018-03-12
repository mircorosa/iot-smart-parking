package smartparking.common;

/**
 * Created by mirco on 12/05/17.
 */
public class Ticket {
	//Ticket code pattern: type code(3 letters) + incremental number for the specific type (5 numbers)
	//Eg. NOR00001
	private String code, parkingLot, releaseDate, parkingDate, leavingDate, paymentDate, paymentMethod, plate;
	private int dwellTime;
	private double totalCost;

	public Ticket() {
		code = parkingLot = releaseDate = parkingDate = leavingDate = paymentDate = paymentMethod = plate = "";
		dwellTime = -1;
		totalCost = -1;
	}

	public Ticket(String code, String releaseDate) {
		this.code = code;
		this.releaseDate = releaseDate;
		parkingLot = parkingDate = leavingDate = paymentDate = paymentMethod = plate = "";
		dwellTime = -1;
		totalCost = -1;
	}

	public Ticket(String code, String releaseDate, String plate) {
		this.code = code;
		this.releaseDate = releaseDate;
		this.plate = plate;
		parkingLot = parkingDate = leavingDate = paymentDate = paymentMethod = "";
		dwellTime = -1;
		totalCost = -1;
	}

	public Ticket(String code, String parkingLot, String releaseDate, String parkingDate, String leavingDate, String paymentDate, String paymentMethod, int dwellTime, double totalCost, String plate) {
		this.code = code;
		this.parkingLot = parkingLot;
		this.releaseDate = releaseDate;
		this.parkingDate = parkingDate;
		this.leavingDate = leavingDate;
		this.paymentDate = paymentDate;
		this.paymentMethod = paymentMethod;
		this.dwellTime = dwellTime;
		this.totalCost = totalCost;
		this.plate = plate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParkingLot() {
		return parkingLot;
	}

	public void setParkingLot(String parkingLot) {
		this.parkingLot = parkingLot;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getParkingDate() {
		return parkingDate;
	}

	public void setParkingDate(String parkingDate) {
		this.parkingDate = parkingDate;
	}

	public String getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(String leavingDate) {
		this.leavingDate = leavingDate;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getDwellTime() {
		return dwellTime;
	}

	public void setDwellTime(int dwellTime) {
		this.dwellTime = dwellTime;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}
}

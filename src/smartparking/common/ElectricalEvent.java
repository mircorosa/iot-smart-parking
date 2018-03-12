package smartparking.common;

public class ElectricalEvent {

	private String ticketCode, plate, parkingDate, leavingDate;

	public ElectricalEvent() {
		ticketCode = plate = parkingDate = leavingDate = "";
	}

	public ElectricalEvent(String ticketCode, String plate, String parkingDate) {
		this.ticketCode = ticketCode;
		this.plate = plate;
		this.parkingDate = parkingDate;
		leavingDate = "";
	}

	public String getTicketCode() {
		return ticketCode;
	}

	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
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
}

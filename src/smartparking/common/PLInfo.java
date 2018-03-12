package smartparking.common;

/**
 * Created by mirco on 17/05/17.
 */
public class PLInfo {
	private String name, type, ticketCode, plate;
	private int level, number;
	private boolean occupied;

	public PLInfo() {
		name = type = ticketCode = plate = "";
		level = number = -1;
		occupied = false;
	}

	public PLInfo(String name, String type, int level, int number) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.number = number;
		this.ticketCode = "";
		this.occupied = false;
		this.plate = "";
	}

	public PLInfo(String name, String type, int level, int number, String plate) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.number = number;
		this.ticketCode = "";
		this.occupied = false;
		this.plate = plate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTicketCode() {
		return ticketCode;
	}

	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}

	public void occupyParkingLot(String ticketCode) {
		setTicketCode(ticketCode);
		this.occupied=true;
	}

	public void freeParkingLot() {
		setTicketCode("");
		this.occupied=false;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public void emptyPlate() {
		this.plate = "";
	}
}

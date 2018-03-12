package smartparking.common.types;

/**
 * Enum class that defines all different vehicle types.
 */
public enum VehicleType {

	NORMAL("Normal"),
	PRIVATE("Private"),
	EXPECTANT("Expectant"),
	ELECTRIC("Electric"),
	HAND("Hand"),
	MOTO("Moto");

	private final String vehicleName;

	VehicleType(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getVehicleName() {
		return vehicleName;
	}
}

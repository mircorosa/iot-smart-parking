package smartparking.common.types;

/**
 * Enum class that defines all different lot types.
 */
public enum LotType {

	NORMAL("Normal"),
	PRIVATE("Private"),
	EXPECTANT("Expectant"),
	ELECTRIC("Electric"),
	HAND("Hand"),
	MOTO("Moto");

	private final String lotName;

	LotType(String lotName) {
		this.lotName=lotName;
	}

	public String getLotName() {
		return lotName;
	}
}

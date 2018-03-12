package smartparking.common.types;

/**
 * Enum class that defines all different lot types.
 */
public enum PaymentType {

	CREDIT_CARD("Credit_Card"),
	DEBIT_CARD("Debit_Card"),
	PREPAID_CARD("Prepaid_Card"),
	CASH("Cash"),
	ANDROID_PAY("Android_Pay");

	private final String paymentType;

	PaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentType() {
		return paymentType;
	}
}

package smartparking.controldevices.paymentbox;

import org.eclipse.californium.core.CoapObserveRelation;
import smartparking.common.PLInfo;

public class ParkingLotRecord {

	private PLInfo info;
	private CoapObserveRelation observeRelation;

	public ParkingLotRecord() {
	}

	public ParkingLotRecord(PLInfo info, CoapObserveRelation observeRelation) {
		this.info = info;
		this.observeRelation = observeRelation;
	}

	public PLInfo getInfo() {
		return info;
	}

	public void setInfo(PLInfo info) {
		this.info = info;
	}

	public CoapObserveRelation getObserveRelation() {
		return observeRelation;
	}

	public void setObserveRelation(CoapObserveRelation observeRelation) {
		this.observeRelation = observeRelation;
	}
}

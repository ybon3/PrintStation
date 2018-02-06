package com.dtc.printStation.client.dao.event;

import com.google.gwt.event.shared.GwtEvent;

public class TestPrinterFinishEvent extends GwtEvent< TestPrinterFinishHandler> {
	public static final Type< TestPrinterFinishHandler> TYPE = new Type< TestPrinterFinishHandler>();

	public final boolean isSuccess;
	public final String msg;

	public TestPrinterFinishEvent(boolean isSuccess, String msg) {
		this.isSuccess = isSuccess;
		this.msg = msg;
	}

	@Override
	public Type< TestPrinterFinishHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TestPrinterFinishHandler handler) {
		handler.onTestPrinterFinish(this);
	}
}

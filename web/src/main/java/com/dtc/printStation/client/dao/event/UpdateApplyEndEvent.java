package com.dtc.printStation.client.dao.event;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateApplyEndEvent extends GwtEvent< UpdateApplyEndHandler> {
	public static final Type< UpdateApplyEndHandler> TYPE = new Type< UpdateApplyEndHandler>();

	public final boolean isSuccess;
	public final String printerName;
	public final boolean isApply;

	public UpdateApplyEndEvent(boolean isSuccess, String printerName, boolean isApply) {
		super();
		this.isSuccess = isSuccess;
		this.printerName = printerName;
		this.isApply = isApply;
	}

	@Override
	public Type< UpdateApplyEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateApplyEndHandler handler) {
		handler.onUpdateApplyEnd(this);
	}
}

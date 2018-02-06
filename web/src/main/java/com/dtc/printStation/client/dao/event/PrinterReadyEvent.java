package com.dtc.printStation.client.dao.event;

import java.util.List;

import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.event.shared.GwtEvent;

public class PrinterReadyEvent extends GwtEvent< PrinterReadyHandler> {
	public static final Type< PrinterReadyHandler> TYPE = new Type< PrinterReadyHandler>();

	public final List<Printer> data;

	public PrinterReadyEvent(List<Printer> data) {
		this.data = data;
	}

	@Override
	public Type< PrinterReadyHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PrinterReadyHandler handler) {
		handler.onPrinterReady(this);
	}
}

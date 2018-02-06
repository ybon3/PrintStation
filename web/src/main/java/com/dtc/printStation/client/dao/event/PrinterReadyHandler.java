package com.dtc.printStation.client.dao.event;

import com.google.gwt.event.shared.EventHandler;

public interface PrinterReadyHandler extends EventHandler{
	public void onPrinterReady(PrinterReadyEvent event);
}

package com.dtc.printStation.client.dao.event;

import com.google.gwt.event.shared.EventHandler;

public interface TestPrinterFinishHandler extends EventHandler{
	public void onTestPrinterFinish(TestPrinterFinishEvent event);
}

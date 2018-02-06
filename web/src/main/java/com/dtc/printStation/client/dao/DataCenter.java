package com.dtc.printStation.client.dao;

import java.util.List;

import com.dtc.printStation.client.RpcService;
import com.dtc.printStation.client.dao.event.PrinterReadyEvent;
import com.dtc.printStation.client.dao.event.PrinterReadyHandler;
import com.dtc.printStation.client.dao.event.TestPrinterFinishEvent;
import com.dtc.printStation.client.dao.event.TestPrinterFinishHandler;
import com.dtc.printStation.client.dao.event.UpdateApplyEndEvent;
import com.dtc.printStation.client.dao.event.UpdateApplyEndHandler;
import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataCenter {
	static final com.dtc.printStation.client.RpcServiceAsync rpc = GWT.create(RpcService.class);

	private static final SimpleEventBus eventBus = new SimpleEventBus();
	public static void fireEvent(GwtEvent<?> event) {
		eventBus.fireEvent(event);
	}

	// Query Printer
	public static void queryPrinter() {
		rpc.getPrinter(new AsyncCallback<List<Printer>>() {
			@Override
			public void onSuccess(List<Printer> result) {
				fireEvent(new PrinterReadyEvent(result));
			}

			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	public static HandlerRegistration addPrinterReadyHandler(PrinterReadyHandler h) {
		return eventBus.addHandler(PrinterReadyEvent.TYPE, h);
	}

	// Test Printer
	public static void testPrint(String printerName) {
		rpc.testPrinter(printerName, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				fireEvent(new TestPrinterFinishEvent(true, null));
			}
			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new TestPrinterFinishEvent(false, caught.getMessage()));
			}
		});
	}

	public static HandlerRegistration addTestPrinterFinishHandler(TestPrinterFinishHandler h) {
		return eventBus.addHandler(TestPrinterFinishEvent.TYPE, h);
	}

	// Apply change
	public static void updateApply(final String printerName, final boolean isApply) {
		rpc.updateApply(printerName, isApply, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				fireEvent(new UpdateApplyEndEvent(result, printerName, isApply));
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	public static HandlerRegistration addUpdateApplyEndHandler(UpdateApplyEndHandler h) {
		return eventBus.addHandler(UpdateApplyEndEvent.TYPE, h);
	}
}
package com.dtc.printStation.server;

import java.util.List;

import com.dtc.printStation.client.RpcService;
import com.dtc.printStation.server.print.PrinterManager;
import com.dtc.printStation.shared.exception.TestException;
import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcServiceImpl extends RemoteServiceServlet implements RpcService {
	private static final long serialVersionUID = 1L;
	private PrinterManager printerManager = PrinterManager.getInstance();

	@Override
	public List<Printer> getPrinter() {
		return printerManager.getPrinter();
	}

	@Override
	public void testPrinter(String printerName) throws TestException {
		try {
			printerManager.testPrinter(printerName);
		} catch (Exception e) {
			throw new TestException(e.getMessage());
		}
	}

	@Override
	public boolean updateApply(String printerName, boolean isApply) {
		return printerManager.updateApply(printerName, isApply);
	}
}

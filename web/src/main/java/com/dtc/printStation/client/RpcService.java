package com.dtc.printStation.client;

import java.util.List;

import com.dtc.printStation.shared.exception.TestException;
import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("RPC")
public interface RpcService extends RemoteService{
	/**
	 * 取得印表機列表
	 */
	List<Printer> getPrinter();

	/**
	 * 測試列印（空白頁）
	 */
	void testPrinter(String printerName) throws TestException;

	/**
	 * 開放 / 禁止使用
	 */
	boolean updateApply(String printerName, boolean isApply);
}

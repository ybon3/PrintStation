package com.dtc.printStation.shared.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TestException extends Exception implements IsSerializable {
	private static final long serialVersionUID = 1L;

	TestException() {}	//Just for GWT RPC

	public TestException(String message) {
		super(message);
	}
}

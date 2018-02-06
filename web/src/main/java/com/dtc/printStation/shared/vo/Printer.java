package com.dtc.printStation.shared.vo;

import java.io.Serializable;

public class Printer implements Serializable {
	private static final long serialVersionUID = -5567681578512197073L;

	private String name;
	private String portName;
	private boolean apply;

	Printer() {}

	public Printer(String name, boolean apply) {
		this.name = name;
		this.apply = apply;
	}

	public Printer(String name, String portName, boolean apply) {
		this.name = name;
		this.portName = portName;
		this.apply = apply;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public boolean isApply() {
		return apply;
	}

	public void setApply(boolean apply) {
		this.apply = apply;
	}
}

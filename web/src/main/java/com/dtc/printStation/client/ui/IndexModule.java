package com.dtc.printStation.client.ui;

import com.sencha.gxt.widget.core.client.TabPanel;

public class IndexModule extends TabPanel {
	public IndexModule() {
		add(new PrinterMaintainView(), "印表機管理");//I18N
	}
}

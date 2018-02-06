package com.dtc.printStation.client;

import com.dtc.printStation.client.ui.IndexModule;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class StationEP implements EntryPoint {
	private static Viewport vp = new Viewport();

	@Override
	public void onModuleLoad() {
		dispatch(Location.getPath());
	}

	private void dispatch(String path) {
		if (path.endsWith("/") || path.endsWith("index.html")) {
			show(new IndexModule());
		} else {	//意料外的網頁
			//這裡預設 GWT compile 的結果是在 webapp 的根目錄
			Location.assign(GWT.getModuleBaseURL()+"..");
		}
	}

	private void show(Widget view) {
		vp.add(view);
		RootPanel.get().add(vp);
	}
}

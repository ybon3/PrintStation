package com.dtc.printStation.client.ui;

import com.dtc.printStation.client.dao.DataCenter;
import com.dtc.printStation.client.dao.event.PrinterReadyEvent;
import com.dtc.printStation.client.dao.event.PrinterReadyHandler;
import com.dtc.printStation.client.dao.event.TestPrinterFinishEvent;
import com.dtc.printStation.client.dao.event.TestPrinterFinishHandler;
import com.dtc.printStation.client.dao.event.UpdateApplyEndEvent;
import com.dtc.printStation.client.dao.event.UpdateApplyEndHandler;
import com.dtc.printStation.client.ui.component.PrinterGrid;
import com.dtc.printStation.client.ui.component.PrinterGrid.ApplySelectEvent;
import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.info.Info;

public class PrinterMaintainView extends Composite {
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	interface MyUiBinder extends UiBinder<Widget, PrinterMaintainView> {}

	@UiField(provided = true) Status rowCount = new Status(GWT.<StatusAppearance> create(BoxStatusAppearance.class));
	@UiField PrinterGrid grid;

	private ListStore<Printer> store;

	public PrinterMaintainView() {
		initWidget(uiBinder.createAndBindUi(this));

		store = grid.getStore();
		DataCenter.addPrinterReadyHandler(new PrinterReadyHandler() {
			@Override
			public void onPrinterReady(PrinterReadyEvent event) {
				store.clear();
				store.addAll(event.data);
				rowCount.setText("Total: " + event.data.size());
				unmask();
			}
		});
		DataCenter.addTestPrinterFinishHandler(new TestPrinterFinishHandler() {
			@Override
			public void onTestPrinterFinish(TestPrinterFinishEvent event) {
				if (event.isSuccess) {
					Window.alert("測試成功"); //I18N
				} else {
					Window.alert("測試失敗：" + event.msg); //I18N
				}
				unmask();
			}
		});
		DataCenter.addUpdateApplyEndHandler(new UpdateApplyEndHandler() {
			@Override
			public void onUpdateApplyEnd(UpdateApplyEndEvent event) {
				if (event.isSuccess) {
					Info.display(event.printerName, event.isApply ? "已開放" : "已禁用"); //I18N
				} else {
					Info.display(event.printerName, "變更作業失敗"); //I18N
				}

				loadPrinter();
			}
		});

		loadPrinter();
	}

	private void loadPrinter() {
		mask("Loading...");
		DataCenter.queryPrinter();
	}

	@UiHandler("grid")
	public void onBtnPrintClick(SelectionEvent<Printer> event) {
		mask("Testing...");
		DataCenter.testPrint(event.getSelectedItem().getName());
	}

	@UiHandler("grid")
	public void onApplySelectClick(ApplySelectEvent event) {
		mask("Updating...");
		DataCenter.updateApply(event.item.getName(), event.selectValue);
	}
}

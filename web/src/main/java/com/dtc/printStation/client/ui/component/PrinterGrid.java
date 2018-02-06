package com.dtc.printStation.client.ui.component;

import java.util.ArrayList;

import com.dtc.printStation.shared.vo.Printer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import us.dontcareabout.gxt.client.model.GetValueProvider;

/**
 *
 */
public class PrinterGrid extends Grid<Printer> implements HasSelectionHandlers<Printer> {
	private static final PrinterProperties props = GWT.create(PrinterProperties.class);
	public static interface PrinterProperties extends PropertyAccess<Printer> {
		@Path("name")
		ModelKeyProvider<Printer> key();
		ValueProvider<Printer, String> name();
		ValueProvider<Printer, String> portName();
		ValueProvider<Printer, Boolean> apply();
	}

	public static final ValueProvider<Printer, String> print = new GetValueProvider<Printer, String>() {
		@Override
		public String getValue(Printer object) {
			return "列印測試"; //I18N
		}
	};

	public PrinterGrid() {
		super(new ListStore<>(props.key()), genColumnModel());

		int lastCellIndex = getColumnModel().getColumnCount() - 1;
		((TextButtonCell)getColumnModel().<String>getCell(lastCellIndex))
			.addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						SelectionEvent.fire(
							PrinterGrid.this,
							getStore().get(event.getContext().getIndex())
						);
					}
				}
			);

		((ComboBoxCell<Boolean>)getColumnModel().<Boolean>getCell(lastCellIndex-1))
			.addSelectionHandler(
				new SelectionHandler<Boolean>() {
					@Override
					public void onSelection(SelectionEvent<Boolean> event) {
						CellSelectionEvent<Boolean> sel = (CellSelectionEvent<Boolean>) event;
						fireEvent(
							new ApplySelectEvent(
								getStore().get(sel.getContext().getIndex()),
								sel.getSelectedItem()
							)
						);
					}
				}
			);

		getView().setForceFit(true);
	}

	private static ColumnModel<Printer> genColumnModel() {
		ArrayList<ColumnConfig<Printer, ?>> list = new ArrayList<>();
		ColumnConfig<Printer, String> btnColumn = new ColumnConfig<>(print, 20, "");
		btnColumn.setCell(new TextButtonCell());

		ListStore<Boolean> applyOptions = new ListStore<>(new ModelKeyProvider<Boolean>() {
			@Override
			public String getKey(Boolean item) {
				return item.toString();
			}
		});
		applyOptions.add(Boolean.TRUE);
		applyOptions.add(Boolean.FALSE);

		ColumnConfig<Printer, Boolean> applyColumn = new ColumnConfig<>(props.apply(), 20, "開放使用"); //I18N
		ComboBoxCell<Boolean> applyCombo = new ComboBoxCell<>(applyOptions, new LabelProvider<Boolean>() {
			@Override
			public String getLabel(Boolean item) {
				return item ? "是" : "否";
			}

		});
		applyCombo.setTriggerAction(TriggerAction.ALL);
		applyCombo.setWidth(60);
		applyColumn.setCell(applyCombo);

		list.add(new ColumnConfig<>(props.name(), 50, "印表機名稱")); //I18N
		list.add(new ColumnConfig<>(props.portName(), 50, "PortName")); //I18N
		list.add(applyColumn);
		list.add(btnColumn);
		return new ColumnModel<>(list);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Printer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	public HandlerRegistration addApplySelectHandler(ApplySelectHandler h) {
		return addHandler(h, ApplySelectEvent.TYPE);
	}

	//ApplySelectEvent & ApplySelectHandler
	public static class ApplySelectEvent extends GwtEvent<ApplySelectHandler> {
		public static final Type<ApplySelectHandler> TYPE = new Type<ApplySelectHandler>();

		public final Printer item;
		public final boolean selectValue;

		public ApplySelectEvent(Printer item, boolean selectValue) {
			this.item = item;
			this.selectValue = selectValue;
		}

		@Override
		public Type<ApplySelectHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ApplySelectHandler handler) {
			handler.onApplySelect(this);
		}
	}

	public static interface ApplySelectHandler extends EventHandler{
		public void onApplySelect(ApplySelectEvent event);
	}
}
package com.dtc.printStation.server.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * 用來測試可列印的空白內容
 */
public class TestPage implements Printable {
	private int pageSize;

	public TestPage() {
		pageSize = 1;
	}

	public TestPage(int pageSize) {
		if (pageSize < 1) {
			throw new IllegalArgumentException("Parameter [pageSize] can't less than 1.");
		}

		this.pageSize = pageSize;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page + 1 > pageSize) { return NO_SUCH_PAGE; }

		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		return PAGE_EXISTS;
	}

}

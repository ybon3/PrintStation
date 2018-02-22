package com.dtc.printStation.server.lab;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import com.dtc.printStation.server.print.PrintItem;
import com.dtc.printStation.server.print.foxit.Common;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFDocument;

public class LabUtil {
	/**
	 * Foxit Version
	 * @throws PDFException
	 */
	private static void printPDF(PrintService service, PrintItem item) throws PrintException {
		PDFDocument document = Common.openDocument(item.getBinary(), "");
		PDFDocPrintable printable = new PDFDocPrintable(document);
		print(service, printable);
	}

	public static void printJPG(PrintService service, PrintItem item) throws PrintException {
		print(service, new ImagePrintable(item.getBinary()));
	}

	public static void printJPG2(PrintService service, PrintItem item) throws PrintException {
		try {
			final BufferedImage image = ImageIO.read(new ByteArrayInputStream(item.getBinary()));

			DocPrintJob printJob = service.createPrintJob();
			printJob.print(
					new SimpleDoc(new Printable() {
						@Override
						public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
							System.out.println("page: " + pageIndex);
							if (pageIndex != 0) {
								return NO_SUCH_PAGE;
							}
							graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
							return PAGE_EXISTS;
						}
					}, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null)
					, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(PrintService service, Printable printable) throws PrintException {
		DocPrintJob printJob = service.createPrintJob();
		printJob.print(new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null), null);
	}
}

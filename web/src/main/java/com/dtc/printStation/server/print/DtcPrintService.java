package com.dtc.printStation.server.print;

import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 * Wrap {@link PrintService}
 */
public class DtcPrintService {
	private PrintService service;

	DtcPrintService(PrintService service) {
		this.service = service;
	}

	public void print(PrintItem item) throws PrintException, IOException {
		switch (item.getFormat()) {
		case PDF:
			//Use PDFBox
			PDDocument document = PDDocument.load(item.getBinary());
			print(new PDFPageable(document));
			document.close();
			break;
		case PNG:
			print(new SimpleDoc(new ByteArrayInputStream(item.getBinary()), DocFlavor.INPUT_STREAM.PNG, null));
			break;
		case JPG:
			print(new SimpleDoc(new ByteArrayInputStream(item.getBinary()), DocFlavor.INPUT_STREAM.JPEG, null));
			break;
		default: throw new PrintException("Unsupported MIME type: " + item.getMimeType());
		}
	}

	public void print(Printable printable) throws PrintException {
		print(new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null));
	}

	public void print(Pageable pageable) throws PrintException {
		print(new SimpleDoc(pageable, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null));
	}

	public void print(Doc doc) throws PrintException {
		DocPrintJob printJob = service.createPrintJob();
		printJob.print(doc, null);
	}

	public static enum SupportFormat {
		PDF("application/pdf"),
		PNG("image/png"),
		JPG("image/jpeg");

		public final String mimeType;

		SupportFormat(String t) {
			mimeType = t;
		}

		public static SupportFormat fromValue(String v) {
			for (SupportFormat c: SupportFormat.values()) {
				if (c.mimeType.equals(v)) {
					return c;
				}
			}

			return null;
		}
	}
}



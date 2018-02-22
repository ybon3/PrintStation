package com.dtc.printStation.zoo;

import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;

import org.apache.commons.io.FileUtils;

import com.dtc.printStation.server.print.PrinterManager;

/**
 * 使用 Microsoft XPS 列印時，跳過檔案 dialog 的範例
 */
public class TestPrintAPI {
	public static void main(String[] args) throws PrintException, IOException, PrinterException {
		File srcFile = new File("D:/blue.jpg");
		File outputFile = new File("D:/TestPrintAPI.xps");
		PrintService service = PrinterManager.findPrintService("Microsoft XPS Document Writer");

		PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		attributes.add(new Destination(outputFile.toURI()));

		DocPrintJob printJob = service.createPrintJob();
		ByteArrayInputStream bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(srcFile));
		printJob.print(new SimpleDoc(bais, DocFlavor.INPUT_STREAM.JPEG, null), attributes);
	}
}

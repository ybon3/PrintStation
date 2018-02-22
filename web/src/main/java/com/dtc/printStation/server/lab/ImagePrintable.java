package com.dtc.printStation.server.lab;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * 由於目前用 Java API 的方式去列印圖檔會有過多的留邊，
 * 所以嘗試自己實作 Printable 來改善
 * <p>
 * 這是一個未完成的 class
 */
public class ImagePrintable implements Printable {
	private BufferedImage image;

	public ImagePrintable(byte[] content) {
		try {
			this.image = ImageIO.read(new ByteArrayInputStream(content));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		System.out.println("page: " + pageIndex);

		if (pageIndex != 0) {
			 return NO_SUCH_PAGE;
		}

		Graphics2D graphics2D = (Graphics2D)graphics;

		// the imageable area is the area within the page margins
		final double imageableWidth = pageFormat.getImageableWidth();
		final double imageableHeight = pageFormat.getImageableHeight();

		double scale = 1;

		// scale to fit
		double scaleX = imageableWidth / image.getWidth();
		double scaleY = imageableHeight / image.getHeight();
		scale = Math.min(scaleX, scaleY);

		// set the graphics origin to the origin of the imageable area (i.e the margins)
		graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		graphics2D.translate(
				(imageableWidth - image.getWidth() * scale) / 2,
				(imageableHeight - image.getHeight() * scale) / 2);

		// draw to graphics using PDFRender
		AffineTransform transform = (AffineTransform)graphics2D.getTransform().clone();
		graphics2D.setBackground(Color.WHITE);
		try {
			renderPageToGraphics(pageIndex, graphics2D, (float)scale);
		} catch (IOException e) {
			e.printStackTrace();
		}



		System.out.println("pf: " + pageFormat.getImageableX() + " * " + pageFormat.getImageableY());
		System.out.println("pf: " + pageFormat.getImageableWidth() + " * " + pageFormat.getImageableHeight());

//		process(graphics, pageFormat);
//		draw(graphics, pageFormat);
		return PAGE_EXISTS;
	}

	private void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scale) throws IOException {
		//transform(graphics, page, scale);
		graphics.scale(scale, scale);

		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

		drawPage(graphics, PDRectangle.A4);
	}

	public void drawPage(Graphics g, PDRectangle pageSize) throws IOException {
		Graphics2D graphics = (Graphics2D) g;
		AffineTransform xform = graphics.getTransform();

		graphics.translate(0, pageSize.getHeight());
		graphics.scale(1, -1);

		// adjust for non-(0,0) crop box
		graphics.translate(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());


		graphics = null;
	}

	private void process(Graphics graphics, PageFormat pf) {
		Graphics2D g2d = (Graphics2D)graphics;
		//ex0
		Rectangle2D.Double rectangle = new Rectangle2D.Double(72, 72, 72, 72);
		g2d.draw(rectangle);

		//ex1
//		Rectangle2D.Double rectangle = new Rectangle2D.Double(
//				pf.getImageableX() + 72, pf.getImageableY() + 72, 72, 72);
//		g2d.draw(rectangle);

		//ex2
//		Rectangle2D.Double rectangle = new Rectangle2D.Double(72, 72, 72, 72);
//		g2d.translate(pf.getImageableX(), pf.getImageableY());
//		g2d.draw(rectangle);
	}

	private void draw(Graphics graphics, PageFormat pf) throws PrinterException {
		Graphics2D g2d = (Graphics2D)graphics;
		graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

//		TestUtils.saveToFile(image, "d:/xxxx.jpg");


//		Graphics2D g2d = (Graphics2D)graphics;
//		g2d.drawLine(-20, -20, 999,999);

//		pf.getPaper().setImageableArea(0, 0, 21.59, 27.94); //Letter
//		pf.getPaper().setImageableArea(0, 0, 21, 29.7); //A4
	}

}

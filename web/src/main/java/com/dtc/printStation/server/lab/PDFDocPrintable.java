package com.dtc.printStation.server.lab;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.dtc.printStation.server.print.foxit.Converter;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.image.Bitmap;
import com.foxit.gsdk.pdf.PDFDocument;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.utils.RectF;

public class PDFDocPrintable implements Printable {

	private PDFDocument document;
//	private PDFRenderer renderer;
	private final float dpi = 72;

	public PDFDocPrintable(PDFDocument document) {
		this.document = document;
//		this.renderer = new PDFRenderer();
	}

	public int print2(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		System.out.println("page: " + pageIndex);

		try {
			int pageSize = document.countPages();
			if (pageIndex < 0 || pageIndex >= pageSize) {
				 return NO_SUCH_PAGE;
			}

            Graphics2D graphics2D = (Graphics2D)graphics;

            PDFPage page = document.getPage(pageIndex);
            PDRectangle cropBox = getRotatedCropBox(page);

            // the imageable area is the area within the page margins
            final double imageableWidth = pageFormat.getImageableWidth();
            final double imageableHeight = pageFormat.getImageableHeight();

            double scale = 1;
//            if (scaling != Scaling.ACTUAL_SIZE)
//            {
//                // scale to fit
//                double scaleX = imageableWidth / cropBox.getWidth();
//                double scaleY = imageableHeight / cropBox.getHeight();
//                scale = Math.min(scaleX, scaleY);
//
//                // only shrink to fit when enabled
//                if (scale > 1 && scaling == Scaling.SHRINK_TO_FIT)
//                {
//                    scale = 1;
//                }
//
//                // only stretch to fit when enabled
//                if (scale < 1 && scaling == Scaling.STRETCH_TO_FIT)
//                {
//                    scale = 1;
//                }
//            }

            // set the graphics origin to the origin of the imageable area (i.e the margins)
            graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // center on page
            if (true) {//center) {
                graphics2D.translate((imageableWidth - cropBox.getWidth() * scale) / 2,
                                     (imageableHeight - cropBox.getHeight() * scale) / 2);
            }

            // rasterize to bitmap (optional)
            Graphics2D printerGraphics = null;
            BufferedImage image = null;
            if (dpi > 0)
            {
                float dpiScale = dpi / 72;
                image = new BufferedImage((int)(imageableWidth * dpiScale / scale),
                                          (int)(imageableHeight * dpiScale / scale),
                                          BufferedImage.TYPE_INT_ARGB);

                printerGraphics = graphics2D;
                graphics2D = image.createGraphics();

                // rescale
                printerGraphics.scale(scale / dpiScale, scale / dpiScale);
                scale = dpiScale;
            }

            // draw to graphics using PDFRender
            AffineTransform transform = (AffineTransform)graphics2D.getTransform().clone();
            graphics2D.setBackground(Color.WHITE);
            renderPageToGraphics(pageIndex, graphics2D, (float)scale);

            // draw crop box
//            if (showPageBorder)
//            {
//                graphics2D.setTransform(transform);
//                graphics2D.setClip(0, 0, (int)imageableWidth, (int)imageableHeight);
//                graphics2D.scale(scale, scale);
//                graphics2D.setColor(Color.GRAY);
//                graphics2D.setStroke(new BasicStroke(0.5f));
//                graphics.drawRect(0, 0, (int)cropBox.getWidth(), (int)cropBox.getHeight());
//            }

            // draw rasterized bitmap (optional)
            if (printerGraphics != null)
            {
                printerGraphics.setBackground(Color.WHITE);
                printerGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());
                printerGraphics.drawImage(image, 0, 0, null);
                graphics2D.dispose();
            }

            return PAGE_EXISTS;
        } catch (Exception e) {
            throw new PrinterException(e.getMessage());
		}
    }

	public void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scale)
            throws IOException, PDFException
    {
        PDFPage page = document.getPage(pageIndex);
        // TODO need width/wight calculations? should these be in PageDrawer?

        transform(graphics, page, scale);

        PDRectangle cropBox = trans(page);
        graphics.clearRect(0, 0, (int) cropBox.getWidth(), (int) cropBox.getHeight());

        // the end-user may provide a custom PageDrawer
//        PageDrawerParameters parameters = new PageDrawerParameters(this, page);
//        PageDrawer drawer = createPageDrawer(parameters);
//        drawer.drawPage(graphics, cropBox);
//        drawPage(graphics, cropBox);
    }

	private PDRectangle trans(PDFPage page) throws PDFException {
		RectF rectF = page.getBox(PDFPage.BOX_CROPBOX);
		return new PDRectangle(rectF.left, rectF.bottom, rectF.width(), rectF.height());
	}


	private void transform(Graphics2D graphics, PDFPage page, float scale) throws PDFException {
        graphics.scale(scale, scale);

        // TODO should we be passing the scale to PageDrawer rather than messing with Graphics?
        int rotationAngle = page.getRotation();
        PDRectangle cropBox = trans(page);

        if (rotationAngle != 0)
        {
            float translateX = 0;
            float translateY = 0;
            switch (rotationAngle)
            {
                case 90:
                    translateX = cropBox.getHeight();
                    break;
                case 270:
                    translateY = cropBox.getWidth();
                    break;
                case 180:
                    translateX = cropBox.getWidth();
                    translateY = cropBox.getHeight();
                    break;
               default:
                    break;
            }
            graphics.translate(translateX, translateY);
            graphics.rotate((float) Math.toRadians(rotationAngle));
        }
    }

	PDRectangle getRotatedCropBox(PDFPage page) throws PDFException {
        PDRectangle cropBox = trans(page);
        int rotationAngle = page.getRotation();
        if (rotationAngle == 90 || rotationAngle == 270) {
            return new PDRectangle(cropBox.getLowerLeftY(), cropBox.getLowerLeftX(),
                                   cropBox.getHeight(), cropBox.getWidth());
        } else {
            return cropBox;
        }
    }

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		System.out.println("page: " + pageIndex);

		try {
			int pageSize = document.countPages();
			if (pageIndex + 1 > pageSize) {
				 return NO_SUCH_PAGE;
			}

			PDFPage page = document.getPage(pageIndex);
			Bitmap bitmap = Converter.renderPageToBitmap(page, 5.0f);


			//
			BufferedImage image = bitmap.convertToBufferedImage();

			saveToJpgFile(image, "d:/page-"+pageIndex+".jpg");

			Graphics2D g2d = (Graphics2D)g;
//			g2d.fillArc(0, 0, 50, 50, 0, 45);
//			g2d.fillArc(0, 0, 50, 50, 135, 45);

			AffineTransform transform = (AffineTransform)((Graphics2D)image.getGraphics()).getTransform().clone();
			g2d.drawRenderedImage(image, transform);


			System.out.println(image.getHeight() + " " + image.getWidth());
			System.out.println(pf.getImageableHeight() + " " + pf.getImageableWidth());
			System.out.println(pf.getImageableX() + " " + pf.getImageableY());

//			g2d.translate((double)image.getHeight(), (double)image.getWidth());

//			g2d.drawImage(image, 0, 0, image.getHeight(), image.getWidth(), null);



//			g2d.translate(image.getHeight(), image.getWidth());
//			g2d.drawImage(bitmap.convertToBufferedImage(), 0, 0, image.getHeight(), image.getWidth(), null);


		} catch (PDFException e) {
			e.printStackTrace();
		}

		return PAGE_EXISTS;
	}

	public static void saveToJpgFile(BufferedImage image, String fn) {
		try {
			File outputfile = new File(fn);
			ImageIO.write(image, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

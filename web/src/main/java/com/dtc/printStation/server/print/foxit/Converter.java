package com.dtc.printStation.server.print.foxit;

import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.image.Bitmap;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.gsdk.pdf.RenderContext;
import com.foxit.gsdk.pdf.Renderer;
import com.foxit.gsdk.utils.Matrix;
import com.foxit.gsdk.utils.Rect;
import com.foxit.gsdk.utils.Size;
import com.foxit.gsdk.utils.SizeF;

public class Converter {

	public static Bitmap renderPageToBitmap(PDFPage pPage, float scale){
		Bitmap bmp = null;
		try
		{
			Progress progress = pPage.startParse(PDFPage.RENDERFLAG_NORMAL);
			if(progress != null)
			{
				int ret = Progress.TOBECONTINUED;
				while (ret == Progress.TOBECONTINUED)
				{
					ret = progress.continueProgress(30);
				}
			}
			progress.release();
			SizeF pageSize = pPage.getSize();
			Matrix matrix = new Matrix();
			int width = (int)(pageSize.getWidth() * scale);
			int height = (int)(pageSize.getHeight() * scale);
			matrix = pPage.getDisplayMatrix(0, 0, width, height, 0);
			bmp = Bitmap.create(new Size(width, height), Bitmap.FORMAT_24BPP_BGR, null, 0);
			Rect rect = new Rect(0, 0, width, height);
			bmp.fillRect(0xffffffffL, rect);
			Renderer render = Renderer.create(bmp);
			RenderContext renderContext = RenderContext.create();
			renderContext.setMatrix(matrix);
			renderContext.setFlags(RenderContext.FLAG_ANNOT);
			Progress renderProgress = pPage.startRender(renderContext, render, 0);
			if(renderContext !=null){
				int ret = Progress.TOBECONTINUED;
				while(ret == Progress.TOBECONTINUED ){
					ret = renderProgress.continueProgress(30);
				}
			}
			renderProgress.release();
		}
		catch(PDFException e)
		{
			e.printStackTrace();
			Common.outputErrMsg(e.getLastError(), "Failed to render page to bitmap.");
		}
		return bmp;
	}
}

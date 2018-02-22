package com.dtc.printStation.server.print.foxit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.PDFLibrary;
import com.foxit.gsdk.pdf.PDFDocument;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.gsdk.utils.FileHandler;

public class Common
{
	/** license file */
	private static String license_id = "LU58e1XxmekjwKZCSPGM7DjOeoFMm0GirEc7eOMcAvzyiAFpMnyEHA==";
	private static String unlockCode = "8f3o18FptRkJBDfqO0hiX2EDmGD0P7P1QAdW927xIlqZhp9AcP06v1dz6KUP4F3zfzhJC4z6t1seoWQIuqPJxBSmshNWe3uJx9fpo80I1eLl0c3H/McMuoly5Rdk3Au7NZeqLUbsx+G34/OOm8i1JFUAEFkJ/YL0xKbS3+DwfEwxPDF3nZPXW1grx6mp3HoLPiqhAzPf9xuCeUiTQDQlfagvVgQg8opNBuceA0xGU+YKqwb015b4kWzB/TlBhsEgAehRWI7Rgds3Cjxhy+j6hVLMj9pDt1BkWQMtyIK3YEZZZsVpnz8ssmMyFJs5He0ALb47qviql6PSl1xH+g4gyw7LBHsK4chCYF93nRror1uYwDVWW5pQDrc+ZJS11FQVBt7uWx71nGit1W0muRJ9iQcr6+M/cBze5Ksyau/RoVoVVrmuXT8R/Qta7Fhd4Zr7h0DDyoHfW6dJqijBC0B0GRJY87uZYP28LpAebLmboFmrKyym+In4E0QUYl4bXZqt7oaweYNpTj0QPgD9XDMRcb8PDNLK+uTuJ46OKUpsE+ySp/d+U3fjDS9zzjoxM4bQ2jJI+hpHbWLovUaNR/BODIqD4FVfDiotbTrCDi8HYHxVdbEsyWZoae8pDEKk/o3l1PpEZHuNfqnVQz/2ZbuhNv3qTIx7+Xa08Dk7mIFyOPCJv9i4fRrfdPw7LkryqyqHD0zMvbqdAKsFyHb9HHLMKu4Hfc6Ae2/+E01HhzE1okhh7nhgh4VwFSnjRxSUsS8lSlHvU942i71lODFa/zCPjGsv8qlQlm2zcwZEHVX5h0Dli6dUyvatF2nRPffL4kcuvHkjqeay96Gz9O9TKWcOXcCmEEb0Y7JHj+JVfJ5Z/LM1Dq4GHh3FCL5niePU9cYhyylY02P+h+XFuoZrpISH2Vkk81RDQpqFnC5kfNMKDuDdmVOzc8GwrX2I4T6rQatT6liKeuoADLKxDVPID9BbdWNQJI3k1dJPTEKtImXZqspxzOswxcP8Ev96nnDUPqAbH8rWXfeALgJxoDYrcIPjF2LjrH1MHmxj9i9gNg2k+yhgLXkYoJ/OFD2LNR/SWJVZ5sbGKesDBoJStGGOZA3PZj4kpKQ4d7RZnSxgRAAqeS7yzG5c+7rT7Sv8cK+mj+rKACjltL5vGgWyhJbi1EuJEwcHNiUsJA86lMWP1KboKIVYRPhJh+7g6rnHQwnhUmga9NA+Q8tNLC9yJq8jtCiRe3+A7mwL5eiPvTmI0tpqTcYbAUj3e3GBtpnoCryFnqeV9/FPHtd/fVt2diZ7lIS3PCqrunj1A6BVim6eBL1ssDmRWHysrzmeww0F2TkbsjWjoA4q8+6dhH2vCZPZ8VfJZr/wIvH8ERS9yyU/2NC+H/nohB49bl0R7sZEatVRw+FwtWz/DXyJwdgOEt1lNYHHBjgrdb/RkjRfOcLYsrAQLSrkV1QEVby8A43+YKllHgnFEl45xrCaN0LL9wkxefjoXl39GljhpRziJl/sUNO5pOu2G8DR4NdkrIgrWagA19nbxhmWeKBYguc+B+VIZOJHtcjRuEEllHx1mI0ZGU3lteQMvet0mlTr61ULsBnxPn1y9cpIwA6plvzIksmcbf9+1edeLZG8505xgs0wEqR376xPt3eFGNFxUhLiPcrx63XBsH/vMoYto6Ncsf/PnQMvKPMddSu93sGL8aae0FQAFddZ3tKyXccjM4ZSddD6T9BPszDVFp2h";

	private int memorySize = 10 * 1024 * 1024;
	private boolean scaleable = true;

	private static FileHandler handler = null;
	private static FileOutputStream stream = null;

	public static void openLog(String path)
	{
		File file = new File(path);

		try
		{
			if (!file.exists() || file.isDirectory())
			{
				file.createNewFile();
			}

			stream = new FileOutputStream(file, true);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void outputLog(String log)
	{
		if (log == null || log.length() < 1)
			System.out.println("Please input log content.");

		System.out.println(log);

		if (stream == null)
			return;
		synchronized (stream)
		{

			try
			{
				stream.write(log.getBytes());
				stream.write("\r\n".getBytes());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void closeLog()
	{
		if (stream == null)
			return;
		try
		{
			stream.flush();
			stream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stream = null;
	}

	//this function will output the specified message according to parameter, mainly used in PDFException catching.
	public static void outputErrMsg(int err, String msg) {
		if (err == PDFException.ERRCODE_INVALIDLICENSE)
			outputLog("Invalid license!!! Please check whether the license has related module!!!");
		else
			outputLog(msg + " Error code:" + err);
	}

	public void initlize()
	{
		PDFLibrary pdfLibrary = PDFLibrary.getInstance();
		try
		{
			pdfLibrary.initialize(memorySize, scaleable);
			pdfLibrary.unlock(license_id, unlockCode);
			outputLog("Success: Initialize and unlock the library.");
			int type = pdfLibrary.getLicenseType();
	    	if(type == PDFLibrary.LICENSETYPE_EXPIRED || type == PDFLibrary.LICENSETYPE_INVALID)
	    	{
	    		outputLog("License is invalid or expired!!!");
	    		System.exit(1);
	    	}
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to initlize and unlock the library");
			System.exit(1);// exit
		}
	}

	public void release()
	{
		if (handler != null)
		{
			try
			{
				handler.release();
			}
			catch (PDFException e)
			{
				e.printStackTrace();
				outputErrMsg(e.getLastError(), "Failed to rlease file handle.");
			}
			finally
			{
				PDFLibrary pdfLibrary = PDFLibrary.getInstance();
				pdfLibrary.destroy();
			}
		}
		else {
			PDFLibrary pdfLibrary = PDFLibrary.getInstance();
			pdfLibrary.destroy();
		}
	}

	public static PDFDocument createDocument()
	{
		PDFDocument pdfDocument = null;
		try
		{
			pdfDocument = PDFDocument.create();
			outputLog("Success: Create a PDF document.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to create PDF Doument.");
			System.exit(1);// exit
		}

		return pdfDocument;
	}

	public static PDFDocument openDocument(byte[] binary, String password)
	{
		PDFDocument pdfDocument = null;
		try
		{
			handler = FileHandler.create(binary, FileHandler.FILEMODE_READONLY);
			if (password == null)
			{
				pdfDocument = PDFDocument.open(handler, null);
			}
			else {
				pdfDocument = PDFDocument.open(handler, password.getBytes());
			}
			outputLog("Success: Open an existing PDF document file.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to open PDF Doument.");
			System.exit(1);// exit
		}

		return pdfDocument;
	}

	public static PDFDocument openDocument(String filePath, String password)
	{
		PDFDocument pdfDocument = null;
		try
		{
			handler = FileHandler.create(filePath, FileHandler.FILEMODE_READONLY);
			if (password == null)
			{
				pdfDocument = PDFDocument.open(handler, null);
			}
			else {
				pdfDocument = PDFDocument.open(handler, password.getBytes());
			}
			outputLog("Success: Open an existing PDF document file.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to open PDF Doument.");
			System.exit(1);// exit
		}

		return pdfDocument;
	}

	public static PDFPage getPage(PDFDocument pdfDocument, int index)
	{
		PDFPage page = null;
		try
		{
			page = pdfDocument.getPage(index);
			outputLog("Success: Get a PDF page.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to get PDF Page.");
			System.exit(1);// exit
		}

		return page;
	}

	public static PDFPage createPage(PDFDocument pdfDocument, int index)
	{
		PDFPage page = null;
		try
		{
			page = pdfDocument.createPage(index);
			outputLog("Success: Create a PDF page.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			outputErrMsg(e.getLastError(), "Failed to create PDF Page.");
			System.exit(1);// exit
		}
		return page;
	}

	public static boolean fileExist(String path)
	{
		File file = new File(path);
		if (file.exists() && file.isFile())
			return true;
		else {
			return false;
		}
	}

	public static boolean folderExist(String path)
	{
		File file = new File(path);
		if (file.exists() && file.isDirectory())
			return true;
		else {
			return false;
		}
	}

	public static String getFileName(String path)
	{
		int index = path.lastIndexOf("/");

		String fileName = path.substring(index + 1, path.length());
		return fileName;
	}

	public static String getFileNameNoEx(String filename) {
		String pFileName = null;
		if ((filename != null) && (filename.length() > 0)) {
			File tempFile =new File( filename.trim());
			pFileName = tempFile.getName();
			int dot = pFileName.lastIndexOf('.');
			if ((dot >-1) && (dot < (pFileName.length()))) {
				return pFileName.substring(0, dot);
			}
		}
		return pFileName;
	}

	public static boolean createFloder(String path)
	{
		File file = new File(path);
		if (file.exists() && file.isDirectory()) return true;
		boolean bCreate = false;
		bCreate = file.mkdir();

		return bCreate;
	}

	public static void saveDocument(PDFDocument pdfDocument, String path)
	{
		FileHandler handler = null;
		try
		{
			handler = FileHandler.create(path, FileHandler.FILEMODE_TRUNCATE);
			Progress progress = pdfDocument.startSaveToFile(handler, PDFDocument.SAVEFLAG_OBJECTSTREAM);
			if (progress != null)
			{
				progress.continueProgress(0);
			}

			progress.release();
			handler.release();
			outputLog("Success: Save PDF document.");
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			if (handler != null)
			{
				try
				{
					handler.release();
				}
				catch (PDFException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			outputErrMsg(e.getLastError(), "Failed to save PDF Document.");
			System.exit(1);// exit
		}
	}
}
